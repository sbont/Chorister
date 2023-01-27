import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useAuth } from "@/stores/authStore";

const routes = [
  {
    path: '/home',
    name: 'Home',
    meta: {
      requiresAuth: true
    },
    component: Home
  },
  {
    path: '/signup',
    name: 'SignUp',
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
        component: () => import(/* webpackChunkName: "allsongs" */ '../views/AllSongs.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'by-season/:id',
        name: 'CategorySeason',
        component: () => import(/* webpackChunkName: "songsbycategory" */ '../views/SongsByCategory.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'by-liturgical-moment/:id',
        name: 'CategoryLiturgical',
        component: () => import(/* webpackChunkName: "songsbycategory" */ '../views/SongsByCategory.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'setlist/new',
        name: 'NewSetlist',
        component: () => import(/* webpackChunkName: "setlistdetail" */ '../components/SetlistDetail.vue'),
        meta: {
          requiresAuth: true
        },
      },
      {
        path: 'setlist/:id',
        name: 'Setlist',
        component: () => import(/* webpackChunkName: "setlist" */ '../views/Setlist.vue'),
        meta: {
          requiresAuth: true
        },
        // children : [
        //   {
        //     path: 'texts',
        //     name: 'Export',
        //     component: () => import(/* webpackChunkName: "setlisttextexport" */ '../views/SetlistTextExport.vue'),
        //     meta: {
        //       requiresAuth: true,
        //       hideHeader: true
        //     },
        //   }
        // ]
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
  },
  {
    path: '/mychoir',
    name: 'My Choir',
    component: () => import(/* webpackChunkName: "mychoir" */ '../views/MyChoir.vue')
  },
  {
    path: '/repertoire/setlist/:id/texts',
    name: 'Export',
    component: () => import(/* webpackChunkName: "setlisttextexport" */ '../views/SetlistTextExport.vue'),
    meta: {
      requiresAuth: true,
      hideHeader: true
    },
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: routes,
  linkExactActiveClass: "is-active",
})

router.beforeEach((to, from, next) => {
  const auth = useAuth();
  // This isn't an actual route leading to a component. It is called by the OAuth server once the user logged in.
  // Handling it here prevents us to have an additional callback.html file. An additional file would lead to a short hiccup after logging in.
  // So here we handle the login redirect and then send the user to the "/" route.
  if (to.path === '/authorized') {
    console.log('Login AFTERR');
    // Inform the authentication of the login redirect. Afterwards we send the user to the main page
    auth.handleLoginRedirect()
      .then(() => next('/home'))
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
  } else if(to.matched.some(record => record.meta.requiresAuth)) {
    if(!auth.isLoggedIn) {
      auth.login()
      .then(() => {
        console.log('Login successful');
        next()
      })
    } else {
      next()
    }
  } else {
    // Default case. The user is send to the desired route.
    next()
  }
})

export default router
