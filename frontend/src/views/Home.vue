<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png" v-if="isLoggedIn">
      {{ user }}
      {{ isLoggedIn }}
  </div>
</template>

<script>
import { useAuth } from "@/stores/authStore";
import {inject, onMounted, ref} from "vue";
import { useRoute, useRouter } from "vue-router";
import {storeToRefs} from "pinia";

export default {
    setup() {
        const logger = inject('vuejs3-logger');
        const route = useRoute()
        const router = useRouter()
        const auth = useAuth()

        // State
        const { user, isLoggedIn } = storeToRefs(auth)

        // Mounted
        onMounted(() => {
        })

        // Methods

        return { user, isLoggedIn }
    }

}
</script>
