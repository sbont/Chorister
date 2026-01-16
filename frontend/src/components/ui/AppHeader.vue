<template>
    <b-navbar>
        <template #brand>
            <b-navbar-item tag="router-link" :to="{ path: '/' }">
                <img src="@/assets/logo.png" alt="Chorister logo" />
            </b-navbar-item>
            <div class="has-text-primary is-size-7" style="margin-top: 0.5rem; margin-left: -32px; z-index: 2">
                beta
            </div>
        </template>
        <template #start v-if="isLoggedIn">
            <b-navbar-item tag="router-link" :to="{ path: '/planning/upcoming' }"> Planning </b-navbar-item>
            <b-navbar-item tag="router-link" :to="{ path: '/repertoire' }"> Repertoire </b-navbar-item>
            <b-navbar-item tag="router-link" :to="{ path: '/mychoir' }"> My Choir </b-navbar-item>
        </template>

        <template #end>
            <b-navbar-item tag="div">
                <div class="buttons">
                    <a v-if="!isLoggedIn" class="button is-primary" @click="onLogin()">
                        <strong>Log in</strong>
                    </a>
                </div>
            </b-navbar-item>

            <b-navbar-dropdown v-if="isLoggedIn" label="Account">
                <b-navbar-item tag="router-link" :to="{ path: '/profile' }"> Profile </b-navbar-item>
                <hr class="navbar-divider" />
                <b-navbar-item href="https://github.com/sbont/Chorister/issues" target="_blank">
                    Report an issue
                </b-navbar-item>
                <b-navbar-item @click="onLogout()">
                    Log out
                </b-navbar-item>
            </b-navbar-dropdown>
        </template>
    </b-navbar>

</template>

<script setup lang="ts">
import { useAuth } from "@/application/authStore";
import { storeToRefs } from "pinia";
import { BNavbar, BNavbarDropdown, BNavbarItem } from "buefy";

// State
const auth = useAuth();
const { isLoggedIn } = storeToRefs(auth);

// Methods
const onLogin = () => auth.login();
const onLogout = () => auth.logout();
</script>