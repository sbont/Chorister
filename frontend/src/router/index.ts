import { createRouter, createWebHistory } from 'vue-router'
import { useAuth } from "@/application/authStore";
import { storeToRefs } from 'pinia';

const routes = [
    {
        path: '/',
        name: 'Landing',
        meta: {
            hideHeader: true,
            forwardWhenAuthenticated: '/planning/upcoming'
        },
        component: () => import('../components/ui/Landing.vue'),
    },
    {
        path: '/signup',
        name: 'SignUp',
        component: () => import('../components/users/SignUp.vue')
    },
    {
        path: '/repertoire',
        component: () => import('../components/songs/Repertoire.vue'),
        meta: {
            requiresAuth: true
        },
        children: [
            {
                path: '',
                name: 'Repertoire',
                component: () => import('../components/songs/Songs.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'category/:id',
                name: 'Category',
                component: () => import('../components/songs/Songs.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'song/new',
                name: 'NewSong',
                component: () => import('../components/songs/SongDetail.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'song/:id',
                name: 'Song',
                component: () => import('../components/songs/SongDetail.vue'),
                meta: {
                    requiresAuth: true
                },
            }
        ]
    },
    {
        path: '/planning',
        name: 'Planning',
        component: () => import('../components/events/Planning.vue'),
        children: [
            {
                path: 'event/new',
                name: 'NewEvent',
                component: () => import('../components/events/EventDetail.vue'),
                meta: {
                    requiresAuth: true
                },
            },
            {
                path: 'event/:id',
                name: 'Event',
                component: () => import('../components/events/Event.vue'),
                meta: {
                    requiresAuth: true
                }
            },
            {
                path: 'upcoming',
                name: ' Upcoming event',
                component: () => import('../components/events/Upcoming.vue'),
                meta: {
                    requiresAuth: true
                }
            }
        ],
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/mychoir',
        name: 'My Choir',
        component: () => import('../components/users/MyChoir.vue')
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('../components/users/Profile.vue')
    },
    {
        path: '/planning/event/:id/texts',
        name: 'Export',
        component: () => import('../components/events/EventTextExport.vue'),
        meta: {
            requiresAuth: true,
            hideHeader: true
        },
    },
    {
        path: '/authorized',
        name: 'Authorized',
        component: () => import('../components/ui/Authorized.vue')
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: routes,
    linkExactActiveClass: "is-active",
})

router.beforeEach((to, from, next) => {
    const auth = useAuth();
    const { isLoggedIn } = storeToRefs(auth)

    // This is a 'fake' route that doesn't lead anywhere, but is just to catch the auth redirect
    if (to.path === '/authorized') {
        auth.handleLoginRedirect()
            .then(() => next('/'))
            .catch(error => {
                console.error('Error handling login redirect:', error);
                next('/')
            })
    } else if (to.path === '/logout') {
        // This is similar to the "/callback" route not leading to an actual component but only to handle the logout callback from the authentication server.
        auth.handleLogoutRedirect()
            .then(() => next('/'))
            .catch(error => {
                console.error('Error handling logout redirect:', error);
                next('/')
            })
    } else if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isLoggedIn) {
            console.log('Not logged in - initiating login redirect');
            // Don't call next() here - the login redirect will handle navigation
            auth.login().catch(error => {
                console.error('Login redirect failed:', error);
                next('/'); // Fallback to home if login fails
            });
            return;
        } else {
            console.log('Already authenticated');
            next()
        }
    } else if (to.matched.some(record => record.meta.forwardWhenAuthenticated) && auth.isLoggedIn) {
        next(to.meta.forwardWhenAuthenticated!)
    } else {
        next()
    }
})

export default router
