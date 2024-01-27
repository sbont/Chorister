export { }

declare module 'vue-router' {
    interface RouteMeta {
        hideHeader?: boolean
        requiresAuth?: boolean
        forwardWhenAuthenticated?: string
    }
}