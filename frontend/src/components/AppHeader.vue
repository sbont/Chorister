<template>
  <nav class="navbar p-2" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
        <router-link to="/" class="navbar-item">
            <img src="@/assets/logo.png"
                 alt="Chorister logo"/>
        </router-link>
        <div class="has-text-primary is-size-7" style="margin-top: 5px; margin-left: -32px; z-index: 2;">beta</div>
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
      <div v-if="isLoggedIn" class="navbar-start">
        <router-link to="/repertoire" class="navbar-item"> Repertoire </router-link>
        <router-link to="/mychoir" class="navbar-item"> My Choir </router-link>
      </div>

      <div class="navbar-end">
        <div class="navbar-item">
          <div class="buttons">
            <a v-if="!isLoggedIn" class="button is-primary" @click="onLogin()">
              <strong>Log in</strong>
            </a>
          </div>
        </div>
        <div v-if="isLoggedIn" class="navbar-item has-dropdown is-hoverable">
          <a class="navbar-link"> Account </a>
            <div class="navbar-dropdown">
              <router-link to="/profile" class="navbar-item"> Profile </router-link>
              <hr class="navbar-divider" />
              <a href="https://github.com/sbont/Chorister/issues" class="navbar-item" target="_blank"> Report an issue </a>
              <a @click="onLogout()" class="navbar-item"> Log out </a>
            </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script lang="ts">
import { useAuth } from "@/stores/authStore";
import { inject, onMounted } from "vue";
import { storeToRefs } from "pinia";


export default {
    setup() {
        const logger = inject('vuejs3-logger');

        // State
        const auth = useAuth();
        const { user, isLoggedIn } = storeToRefs(auth);

        // Computed
        onMounted(() => {});

        // Methods
        const onLogin = () => auth.login()
        const onLogout = () => auth.logout()

        return { auth, user, isLoggedIn, onLogin, onLogout }
    }
}
</script>