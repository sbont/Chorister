import { defineStore } from "pinia";
import { User, UserManager, UserManagerSettings, WebStorageStateStore } from 'oidc-client-ts'
import { computed, ref } from "vue";
import { AccessLevel } from "@/types/access-level";
import { Operation } from "@/types/operations";
import { EntityLevelPermissions, EntityType } from "./authorization";

const MetadataScope: string = 'urn:zitadel:iam:user:metadata';
const ProjectsRoleScope: string = 'urn:zitadel:iam:org:projects:roles';
const ProjectIdRoleScope: string = `urn:zitadel:iam:org:project:id:${import.meta.env.VITE_APP_CHORISTER_PROJECT_ID}`;
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
    const accessLevel = ref<AccessLevel>();

    // getters
    const isLoggedIn = computed(() => { 
        console.log(user.value?.access_token);
        console.log(user.value?.refresh_token);
        
        return user.value !== null});

    // actions
    function init() {
        userManager.getUser().then(u => u == null ? user.value = null : setUser(u));
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
        return userManager.signinRedirectCallback().then(setUser);
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

    function setUser(value: User) {
        user.value = value;
        const roles = value.profile[ProjectIdRoleClaim] as Record<number, string>;
        const roleNames = Object.keys(roles);
        const accessLevels = roleNames.map(roleName => {
            const parts = roleName.split('.');
            const accessLevel = parts.at(-1)?.toUpperCase();
            return AccessLevel[accessLevel as keyof typeof AccessLevel];
        });
        accessLevel.value = accessLevels.sort((a, b) => b - a).at(0);
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
        if (accessLevel.value === undefined) 
            return false;

        const minimumAccessLevel = EntityLevelPermissions[entity][operation];
        return accessLevel.value >= minimumAccessLevel;
    }

    return { getAccessToken, handleLoginRedirect, handleLogoutRedirect, init, isLoggedIn, login, logout, removeSession }
});