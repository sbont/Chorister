<template>
    <div class="signup container" v-if="!loading">
        <section class="hero is-medium is-link">
            <div class="hero-body">
                <p class="subtitle">Register as a new chorister user</p>
                <p class="title" v-if="isInvite">{{ choir.name }}</p>
            </div>
        </section>

        <div class="pt-5">
            <div class="field">
                <label class="label">Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Johnny" v-model="registration.displayName"/>
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
            loading: true,
            registration: {},
            isInvite: null,
            choir: {},
			success: null
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
                    this.error = "Failed to load invite";
                    this.loading = false;
                });
        } else {
            this.loading = false;
        }

    },

	methods: {
        submit: function () {
            this.saving = true;
            if (!this.registration) {
                return;
            }
            let action = this.isInvite ? api.acceptInvite : api.register;
			action(this.registration)
				.then((response) => {
					console.log(response);
					this.success = true;
			})
        },
	}
}

export default Signup;
</script>
