<template>
    <div class="signup container">
        <section class="hero is-medium is-link">
            <div class="hero-body">
                <p class="title">Medium hero</p>
                <p class="subtitle">Medium subtitle</p>
            </div>
        </section>
        <img alt="Vue logo" src="../assets/logo.png" />

        <div class="field">
            <label class="label">Name</label>
            <div class="control">
                <input class="input" type="text" placeholder="Johnny" v-model="registration.displayName"/>
            </div>
        </div>

        <div class="field">
            <label class="label">Name of your choir</label>
            <div class="control">
                <input class="input" type="text" placeholder="" v-model="registration.choirName" />
            </div>
        </div>

        <div class="field">
            <label class="label">Email</label>
            <div class="control has-icons-left has-icons-right">
                <input class="input" type="email" placeholder="you@" value="" v-model="registration.email" />
                <span class="icon is-small is-left">
                    <i class="fas fa-envelope"></i>
                </span>
            </div>
        </div>

        <div class="field">
            <label class="label">Password</label>
            <p class="control has-icons-left">
                <input class="input" type="password" placeholder="Password" v-model="registration.password"/>
                <span class="icon is-small is-left">
                    <i class="fas fa-lock"></i>
                </span>
            </p>
        </div>

        <div class="control">
            <button class="button is-primary" @click="submit">Create account</button>
        </div>

		<div v-if="success">
			success		
		</div>
    </div>
</template>

<script>
import api from "../api";

const Signup = {
	name: "SignUp",
	props: [],
	// app initial state
    data: function () {
        return {
            registration: {},
			success: null
        };
    },
	methods: {
        submit: function () {
            this.saving = true;
            if (!this.registration) {
                return;
            }
			api.register(this.registration)
				.then((response) => {
					console.log(response);
					this.success = true;

			})

        },
	}
}

export default Signup;
</script>
