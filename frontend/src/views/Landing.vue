<template>
	<section class="hero is-info is-fullheight">
		
		<div class="hero-head"></div>
		
		<div class="hero-body">
			<div class="container has-text-centered">
				<div class="columns is-mobile is-centered">
					<div class="column is-half">
						<img src="@/assets/logo-w-t.png" alt="Chorister logo"/>
					</div>
				</div>
				<div class="subtitle columns is-mobile is-centered">
					<div class="field has-addons">
						<p class="control">
							<router-link to="/signup" class="button is-info is-inverted is-outlined"> Sign up </router-link>
						</p>
						<p class="control">
							<button class="button is-info is-inverted is-outlined" @click="onLogin()">
								<span>Log in</span>
							</button>
						</p>
					</div>
				</div>
			</div>
		</div>
		
		<div class="hero-foot">
			<nav class="tabs is-boxed is-fullwidth">
				<div class="container">
					<ul>
						<li>
							<a> Features </a>
						</li>
						<li>
							<router-link to="/about"> About </router-link>
						</li>
					</ul>
				</div>
			</nav>
		</div>
	</section>
</template>

<script>
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

        return { auth, user, isLoggedIn, onLogin }
    }
}
</script>