export { }

declare module 'vue-router' {
    interface RouteMeta {
        hideHeader?: boolean
        allowAnonymous?: boolean
        forwardWhenAuthenticated?: string
    }
}