<template>
    <div class="signup container" v-if="!loading">
        <section class="mt-6">
            <h1 class="title has-text-primary">Register as a new user</h1>
            <h2 class="subtitle has-text-info" v-if="isInvite">joining {{ choir.name }}</h2>
        </section>

        <div class="pt-5">
            <div class="field">
                <label class="label">Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Elvis" v-model="registration.displayName"/>
                </div>
            </div>

            <div class="field" v-if="!isInvite">
                <label class="label">Name of your choir</label>
                <div class="control">
                    <input class="input" type="text" placeholder="" v-model="registration.choirName" />
                </div>
            </div>

            <div class="field">
                <label class="label">Email</label>
                <div class="control has-icons-left">
                    <input class="input" type="email" placeholder="you@" v-model="registration.email" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-envelope"></i>
                    </span>
                </div>
            </div>

            <div class="field">
                <label class="label">Password</label>
                <p class="control has-icons-left">
                    <input class="input" type="password" placeholder="Super safe password" v-model="registration.password"/>
                    <span class="icon is-small is-left">
                    <i class="fas fa-lock"></i>
                </span>
                </p>
            </div>

            <div class="control">
                <button 
                    class="button is-primary" 
                    @click="submit"
                    :class="{ 'is-loading': saving }">
                    Create account
                </button>
            </div>

            <div v-if="success" class="has-text-success">
                Account created.
            </div>
            <div v-if="errorMessage" class="has-text-danger">
                {{ errorMessage }}
            </div>
        </div>

    </div>
</template>

<script>
import api from "./../api.js";

const Signup = {
	name: "SignUp",
	props: [],
	// app initial state
    data: function () {
        return {
            loading: true,
            saving: false,
            registration: {},
            isInvite: null,
            choir: {},
			success: null,
            errorMessage: null
        };
    },

    mounted: function () {
        let token = this.$route.query.invite;
        this.isInvite = !!token;
        if (token) {
            api.getInviteByToken(token)
                .then((response) => {
                    this.$log.debug("Invite loaded: ", response.data);
                    let inviteDetail = response.data;
                    this.registration.email = inviteDetail.email;
                    this.registration.token = inviteDetail.token;
                    this.choir = inviteDetail.choir;
                    this.loading = false;
                })
                .catch((error) => {
                    this.$log.debug(error);
                    this.errorMessage = "Failed to load invite";
                    this.loading = false;
                });
        } else {
            this.loading = false;
        }

    },

	methods: {
        submit: function () {
            this.saving = true;
            this.errorMessage = null;
            if (!this.registration) {
                return;
            }
            let action = this.isInvite ? api.acceptInvite : api.register;
			action(this.registration)
				.then((response) => {
					console.log(response);
					this.success = true;
                })
                .catch((error) => {
                    this.errorMessage = error.response.data.message ?? "Error while sending request: " + error.response.statusText;
                    this.$log.debug(error.response);
                })
                .finally(() => this.saving = false);
        },
	}
}

export default Signup;
</script>
