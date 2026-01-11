import { defineStore } from "pinia";
import { User, UserManager, UserManagerSettings, WebStorageStateStore } from 'oidc-client-ts'
import { computed, ref } from "vue";
import { equalsOrGreaterThan, Role } from "@/types/role";
import { Operation } from "@/types/operations";
import { EntityLevelPermissions, EntityType } from "./authorization";

const MetadataScope: string = 'urn:zitadel:iam:user:metadata';
const ProjectsRoleScope: string = 'urn:zitadel:iam:org:projects:roles';
const ProjectIdRoleScope: string = `urn:zitadel:iam:org:project:id:${import.meta.env.VITE_APP_CHORISTER_PROJECT_ID}:aud`;
const ProjectRoleClaim: string = 'urn:zitadel:iam:org:project:roles';
const ProjectIdRoleClaim: string = `urn:zitadel:iam:org:project:${import.meta.env.VITE_APP_CHORISTER_PROJECT_ID}:roles`;

const settings: UserManagerSettings = {
    userStore: new WebStorageStateStore({ store: window.localStorage }),
    authority: import.meta.env.VITE_APP_AUTHORITY_URL,
    client_id: import.meta.env.VITE_APP_CHORISTER_WEB_CLIENT_ID,
    response_type: 'code',
    redirect_uri: import.meta.env.VITE_APP_BASE_URL + '/authorized',
    post_logout_redirect_uri: import.meta.env.VITE_APP_BASE_URL,
    scope: `openid profile offline_access ${MetadataScope} ${ProjectsRoleScope} ${ProjectIdRoleScope}`,
    automaticSilentRenew: true,
    loadUserInfo: true
};

export const useAuth = defineStore('auth', () => {
    const user = ref<User | null>(null);
    const userManager = new UserManager(settings);
    const role = ref<Role>();
    const isLoggingIn = ref(false);

    // getters
    const isLoggedIn = computed(() => {
        const hasUser = user.value !== null;
        const hasAccessToken = user.value?.access_token !== undefined;
        const hasRefreshToken = user.value?.refresh_token !== undefined;
        
        console.log('isLoggedIn check:', {
            hasUser,
            hasAccessToken,
            hasRefreshToken,
            accessToken: hasAccessToken ? 'present' : 'missing',
            refreshToken: hasRefreshToken ? 'present' : 'missing'
        });

        return hasUser && hasAccessToken;
    });

    // actions
    async function init() {
        try {
            const u = await userManager.getUser();
            if (u == null) {
                user.value = null;
            } else {
                setUser(u);
            }
        } catch (error) {
            console.error("Error during auth initialization:", error);
            user.value = null;
        }
        
        userManager.events.addAccessTokenExpired(() => {
            console.log("Access token expired. Logging out...");
            useAuth().removeSession();
        });
    }

    function login() {
        if (isLoggingIn.value) {
            console.log('Login already in progress, ignoring duplicate login attempt');
            return Promise.reject(new Error('Login already in progress'));
        }
        
        isLoggingIn.value = true;
        console.log('Initiating login redirect');
        
        return userManager.signinRedirect()
            .catch(error => {
                console.error('Login redirect failed:', error);
                isLoggingIn.value = false;
                throw error;
            });
    }

    function logout() {
        userManager.signoutRedirect()
            .then(() => {
                console.log('User logged out')
            })
            .catch(error => console.log(error))
    }

    function handleLoginRedirect() {
        return userManager.signinRedirectCallback()
            .then(user => {
                console.log('Login redirect callback successful, setting user');
                setUser(user);
                isLoggingIn.value = false;
                return user;
            })
            .catch(error => {
                console.error('Error in login redirect callback:', error);
                isLoggingIn.value = false;
                throw error;
            });
    }

    function handleLogoutRedirect() {
        return userManager.signoutRedirectCallback()
            .then(() => {
                console.log('Logout redirect callback successful, clearing user');
                resetUser();
            })
            .catch(error => {
                console.error('Error in logout redirect callback:', error);
                resetUser();
                throw error;
            });
    }

    function getUser() {
        return new Promise((resolve, reject) => {
            userManager.getUser()
                .then(resolve)
                .catch(reject)
        });
    }

    function setUser(value: User) {
        user.value = value;
        const roleClaims = value.profile[ProjectIdRoleClaim] as Record<number, string>;
        if (!roleClaims)
            return;

        const roleNames = Object.keys(roleClaims);
        const roles = roleNames.map(roleName => {
            const parts = roleName.split('.');
            const role = parts.at(-1)?.toUpperCase();
            return role as Role;
        });
        role.value = roles.at(0);
    }

    function resetUser() {
      user.value = null;
      role.value = undefined;
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
        user.value = null;
        return userManager.signinRedirect();
    }

    function getUserZitadelId() {
        return user.value?.profile?.sub;
    }

    function userCan(operation: Operation, entity: EntityType): boolean {
        if (role.value === undefined)
            return false;

        const minimumAccessLevel = EntityLevelPermissions[entity][operation];
        return equalsOrGreaterThan(role.value, minimumAccessLevel);
    }

    return { role, isLoggedIn, getAccessToken, handleLoginRedirect, handleLogoutRedirect, init, login, logout, removeSession, userCan }
});