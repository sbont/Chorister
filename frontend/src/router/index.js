import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import { authService } from '../auth'


Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/signup',
    name: 'Sign Up',
    component: () => import(/* webpackChunkName: "signup" */ '../views/SignUp.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  },
  {
    path: '/repertoire',
    component: () => import(/* webpackChunkName: "repertoire" */ '../views/Repertoire.vue'),
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Repertoire',
        component: () => import(/* webpackChunkName: "songs" */ '../components/Songs.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'song/new',
        name: 'NewSong',
        component: () => import(/* webpackChunkName: "song" */ '../components/Song.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'song/:id',
        name: 'Song',
        component: () => import(/* webpackChunkName: "song" */ '../components/Song.vue'),
        meta: {
          requiresAuth: true
        },
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  // This isn't an actual route leading to a component. It is called by the OAuth server once the user logged in. Handling it her prevents us to have an additional callback.html file. An additional file would lead to a short hick-up after logging in. (callback.html is loaded and than the actual route.)
  // So here we handle the login redirect and than send the user to the "/" route.
  if (to.path === '/authorized') {
    console.log('Login AFTERR');
    // Inform the authentication of the login redirect. Afterwards we send the user to the main page
    authService.handleLoginRedirect()
      .then(() => next('/'))
      .catch(error => {
        console.log(error)
        next('/')
      })
  } else if (to.path === '/logout') {
  // This is similar to the "/callback" route not leading to an actual component but only to handle the logout callback from the authentication server.
    authService.handleLogoutRedirect()
      .then(() => next('/'))
      .catch(error => {
        console.log(error)
        next('/')
      })
  } else if(to.matched.some(record => record.meta.requiresAuth)) {
    authService.isUserLoggedIn().then(isLoggedIn => {
      if(!isLoggedIn) {
        authService.login()
        .then(() => {
          console.log('Login successful');
        })
      } else {
        next()
      }
    });
  } else {
    // Default case. The user is send to the desired route.
    next()
  }
})

export default router
