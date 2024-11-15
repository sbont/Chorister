import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from "@/stores/authStore";
import { children } from 'effect/Fiber';

const routes = [
    {
        path: '/',
        name: 'Landing',
        meta: {
            hideHeader: true,
            forwardWhenAuthenticated: '/repertoire'
        },
        component: () => import('../components/Landing.vue'),
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: () => import('../components/SignUp.vue')
    },
    {
        path: '/repertoire',
        component: () => import('../components/Repertoire.vue'),
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: '',
                name: 'Repertoire',
                component: () => import('../components/AllSongs.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'by-season/:id',
                name: 'CategorySeason',
                component: () => import('../components/SongsByCategory.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'by-liturgical-moment/:id',
                name: 'CategoryLiturgical',
                component: () => import('../components/SongsByCategory.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'song/new',
                name: 'NewSong',
                component: () => import('../components/Song.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'song/:id',
                name: 'Song',
                component: () => import('../components/Song.vue'),
                meta: {
                    requiresAuth: true
                },
            }
        ]
    },
    {
        path: '/planning',
        name: 'Planning',
        component: () => import('../components/Planning.vue'),
        children: [
            {
                path: 'event/new',
                name: 'NewEvent',
                component: () => import('../components/EventDetail.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'event/:id',
                name: 'Event',
                component: () => import('../components/Event.vue'),
                meta: {
                    requiresAuth: true
                }
            },
        ],
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/mychoir',
        name: 'My Choir',
        component: () => import('../components/MyChoir.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('../components/Profile.vue')
    },
    {
        path: '/repertoire/event/:id/texts',
        name: 'Export',
        component: () => import('../components/EventTextExport.vue'),
        meta: {
            requiresAuth: true,
            hideHeader: true
        },
    },
    {
        path: '/authorized',
        name: 'Authorized',
        component: () => import('../components/Authorized.vue')
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes,
    linkExactActiveClass: "is-active",
})

router.beforeEach((to, from, next) => {
    console.log(to)
    const auth = useAuth();
    // This isn't an actual route leading to a component. It is called by the OAuth server once the user logged in.
    // Handling it here prevents us to have an additional callback.html file. An additional file would lead to a short hiccup after logging in.
    // So here we handle the login redirect and then send the user to the "/" route.
    if (to.path === '/authorized') {
        // Inform the authentication of the login redirect. Afterwards we send the user to the main page
        auth.handleLoginRedirect()
            .then(() => next('/'))
            .catch(error => {
                console.log(error)
                next('/')
            })
    } else if (to.path === '/logout') {
        // This is similar to the "/callback" route not leading to an actual component but only to handle the logout callback from the authentication server.
        auth.handleLogoutRedirect()
            .then(() => next('/'))
            .catch(error => {
                console.log(error)
                next('/')
            })
    } else if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!auth.isLoggedIn) {
            auth.login()
                .then(() => {
                    console.log('Login successful');
                    next()
                })
        } else {
            next()
        }
    } else if (to.matched.some(record => record.meta.forwardWhenAuthenticated) && auth.isLoggedIn) {
        next(to.meta.forwardWhenAuthenticated!)
    } else {
        // Default case. The user is send to the desired route.
        next()
    }
})

export default router
