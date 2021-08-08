import { UserManager, WebStorageStateStore, Log } from 'oidc-client'


const settings = {
  userStore: new WebStorageStateStore({ store: window.sessionStorage }),
  authority: 'http://localhost:8080/auth/realms/Chorister',
  client_id: 'chorister-web',
  response_type: 'code',
  redirect_uri: 'http://localhost:8080/authorized',
  post_logout_redirect_uri: 'http://localhost:8080/logout',
  scope: 'openid cms',
  automaticSilentRenew: true
}

let userManager = new UserManager(settings)

class AuthService {
    
    login () {
      userManager.signinRedirect()
        .catch(error => console.log(error))
    }
  
    logout () {
      userManager.signoutRedirect()
        .then(() => console.log('User logged out'))
        .catch(error => console.log(error))
    }
  
    handleLoginRedirect () {
      return userManager.signinRedirectCallback()
    }
  
    handleLogoutRedirect () {
      return userManager.signoutRedirectCallback()
    }
  
    isUserLoggedIn () {
      return new Promise((resolve, reject) => {
        userManager.getUser()
          .then(user => {
            if (user === null) {
              resolve(false)
            }
            resolve(true)
          })
          .catch(error => reject(error))
      })
    }
    
    getProfile () {
      return new Promise((resolve, reject) => {
        userManager.getUser()
          .then(user => {
            if (user === null) {
              resolve(null)
            }
            resolve(user.profile)
        })
        .catch(error => reject(error))
        })
    }

    getAccessToken () {
        return new Promise((resolve, reject) => {
        userManager.getUser()
            .then(user => {
            console.log('Got access token from user')
            resolve(user.access_token)
            })
            .catch(error => reject(error))
        })
    }
}

export const authService = new AuthService()

export default {
    install: function (Vue) {
        Log.logger = console;
        Log.level = Log.INFO;
        Vue.prototype.$auth = authService
    }
}