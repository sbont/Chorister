<template>
  <nav class="navbar p-2" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img
          src="@/assets/logo.png"
        />
      </a>

      <a
        role="button"
        class="navbar-burger"
        aria-label="menu"
        aria-expanded="false"
        data-target="navbarBasicExample"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div v-if="authenticated" class="navbar-start">
        <router-link to="/repertoire" class="navbar-item"> Repertoire </router-link>
        <router-link to="/mychoir" class="navbar-item"> My Choir </router-link>
        <div class="navbar-item has-dropdown is-hoverable">
          <a class="navbar-link"> Settings </a>
          <div class="navbar-dropdown">
            <router-link to="/categories" class="navbar-item"> Categories </router-link>
            <hr class="navbar-divider" />
            <router-link to="/about" class="navbar-item"> About </router-link>
            <router-link to="/report" class="navbar-item"> Report an issue </router-link>
          </div>
        </div>
      </div>

      <div class="navbar-end">
        <div class="navbar-item">
          <div class="buttons">
            <a v-if="!authenticated" class="button is-primary" @click="onLogin()">
              <strong>Log in</strong>
            </a>
          </div>
        </div>
        <div v-if="authenticated" class="navbar-item has-dropdown is-hoverable">
          <a class="navbar-link"> Account </a>
            <div class="navbar-dropdown">
              <router-link to="/profile" class="navbar-item"> Profile </router-link>
              <hr class="navbar-divider" />
              <router-link to="/about" class="navbar-item"> About </router-link>
              <a @click="onLogout()" class="navbar-item"> Log out </a>
            </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { authService} from "@/auth";

export default {
  name: 'Home',
  data () {
    return {
      authenticated: false
    }
  },
  mounted () {
    authService.isUserLoggedIn()
      .then(isUserLoggedIn => {
        this.authenticated = isUserLoggedIn
      })
      .catch(error => {
        console.log(error)
        this.authenticated = false
      })
  },
  // watch: {
  //   '$route': 'refreshIsUserLoggedIn'
  // },
  methods: {
    onLogin () {
      authService.login()
    },
    onLogout () {
      authService.logout()
    },
    async refreshIsAuthenticated () {
      this.authenticated = await authService.isUserLoggedIn()
    }
  }
}
</script>