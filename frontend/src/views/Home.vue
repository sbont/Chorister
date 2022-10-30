<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png" v-if="userLoggedIn">
      {{ user }}
      {{ userLoggedIn}}
  </div>
</template>

<script>
import { authService } from '@/auth'
import {inject, onMounted, ref} from "vue";
import { useRoute, useRouter } from "vue-router";

export default {
    setup() {
        const logger = inject('vuejs3-logger');
        const route = useRoute()
        const router = useRouter()

        // State
        let userLoggedIn = false;
        let user = null;

        // Mounted
        onMounted(() => {
            authService.isUserLoggedIn().then(v => userLoggedIn = v);
            authService.getUser().then(v => user = v);
        })

        // Methods


        return { userLoggedIn, user }
    }

}
</script>
