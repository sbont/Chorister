import { defineStore } from "pinia";
import { User, UserManager, UserManagerSettings, WebStorageStateStore } from 'oidc-client-ts'
import { computed, ref } from "vue";

const settings: UserManagerSettings = {
    userStore: new WebStorageStateStore({ store: window.localStorage }),
    authority: import.meta.env.VITE_APP_AUTHORITY_URL,
    client_id: import.meta.env.VITE_APP_CHORISTER_WEB_CLIENT_ID,
    response_type: 'code',
    redirect_uri: import.meta.env.VITE_APP_BASE_URL + '/authorized',
    post_logout_redirect_uri: import.meta.env.VITE_APP_BASE_URL,
    scope: `openid profile offline_access urn:zitadel:iam:user:metadata urn:zitadel:iam:org:projects:roles urn:zitadel:iam:org:project:id:${import.meta.env.VITE_APP_CHORISTER_PROJECT_ID}:aud`,
    automaticSilentRenew: true
};

export const useAuth = defineStore('auth', () => {
    const user = ref<User | null>(null);
    const userManager = new UserManager(settings)

    // getters
    const isLoggedIn = computed(() => { 
        console.log(user.value?.access_token);
        console.log(user.value?.refresh_token);
        
        return user.value !== null});

    // actions
    function init() {
        userManager.getUser().then(u => user.value = u);
        userManager.events.addAccessTokenExpired(() => {
            console.log("Access token expired. Logging out...");
            useAuth().removeSession();
        });
    }

    function login() {
        return userManager.signinRedirect();
    }

    function logout() {
        userManager.signoutRedirect()
            .then(() => {
                console.log('User logged out')
            })
            .catch(error => console.log(error))
    }

    function handleLoginRedirect() {
        return userManager.signinRedirectCallback().then(u => user.value = u);
    }

    function handleLogoutRedirect() {
        return userManager.signoutRedirectCallback().then(() => user.value = null);
    }

    function getUser() {
        return new Promise((resolve, reject) => {
            userManager.getUser()
                .then(user => {
                    resolve(user)
                })
                .catch(error => reject(error))
        })
    }

    function getAccessToken() {
        return new Promise((resolve, reject) => {
            userManager.getUser()
                .then(user => {
                    if (user) {
                        resolve(user.access_token)
                    } else {
                        resolve(null)
                    }
                })
                .catch(error => reject(error))
        })
    }

    function removeSession() {
        user.value = null
        return userManager.signinRedirect();
    }

    function getUserZitadelId() {
        return user.value?.profile?.sub;
    }

    return { getAccessToken, handleLoginRedirect, handleLogoutRedirect, init, isLoggedIn, login, logout, removeSession }
});