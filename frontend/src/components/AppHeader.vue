<template>
    <nav class="navbar px-2" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <router-link to="/" class="navbar-item">
                <img src="@/assets/logo.png" alt="Chorister logo" />
            </router-link>
            <div class="has-text-primary is-size-7" style="margin-top: 0.5rem; margin-left: -32px; z-index: 2">
                beta
            </div>
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
                <router-link to="/planning" class="navbar-item">Planning</router-link>
                <router-link to="/repertoire" class="navbar-item">Repertoire</router-link>
                <router-link to="/mychoir" class="navbar-item">My Choir</router-link>
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
                        <router-link to="/profile" class="navbar-item"> Profile</router-link>
                        <hr class="navbar-divider" />
                        <a href="https://github.com/sbont/Chorister/issues" class="navbar-item" target="_blank">
                            Report an issue
                        </a>
                        <a @click="onLogout()" class="navbar-item"> Log out </a>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<script setup lang="ts">
import { useAuth } from "@/services/authStore";
import { storeToRefs } from "pinia";

// State
const auth = useAuth();
const { isLoggedIn } = storeToRefs(auth);

// Methods
const onLogin = () => auth.login();
const onLogout = () => auth.logout();
</script>