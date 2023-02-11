import {defineStore} from "pinia";
import {inject} from "vue";
import {Log, UserManager, WebStorageStateStore} from 'oidc-client'

const logger = inject('vuejs3-logger');
const settings = {
    userStore: new WebStorageStateStore({ store: window.localStorage }),
    authority: 'http://localhost:8080/auth/realms/Chorister',
    client_id: 'chorister-web',
    response_type: 'code',
    redirect_uri: 'http://localhost:8080/authorized',
    post_logout_redirect_uri: 'http://localhost:8080/',
    scope: 'openid cms',
    automaticSilentRenew: true
}
let userManager = new UserManager(settings)
userManager.events.addAccessTokenExpired(() => {
    console.log("Access token expired. Logging out...");
    useAuth().logout();
});

export const useAuth = defineStore('auth', {
    state: () => ({
        user: null
    }),
    getters: {
        isLoggedIn: (state) => state.user !== null
    },
    actions: {
        init() {
            userManager.getUser().then(user => this.user = user);
        },

        login () {
            return userManager.signinRedirect();
        },

        logout () {
            userManager.signoutRedirect()
                .then(() => {
                    console.log('User logged out')
                })
                .catch(error => console.log(error))
        },

        handleLoginRedirect () {
            return userManager.signinRedirectCallback().then((user) => this.user = user);
        },

        handleLogoutRedirect () {
            return userManager.signoutRedirectCallback().then(() => this.user = null);
        },

        getUser() {
            return new Promise((resolve, reject) => {
                userManager.getUser()
                    .then(user => {
                        resolve(user)
                    })
                    .catch(error => reject(error))
            })
        },

        getAccessToken () {
            return new Promise((resolve, reject) => {
                userManager.getUser()
                    .then(user => {
                        if(user) {
                            console.log('Got access token from user')
                            resolve(user.access_token)
                        } else {
                            resolve(null)
                        }
                    })
                    .catch(error => reject(error))
            })
        },
    }
})
export default {
    install: function (Vue) {
        Log.logger = console;
        Log.level = Log.INFO;
    }
}