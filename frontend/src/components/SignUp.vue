<template>
    <div class="signup container" v-if="state != State.Loading">
        <section class="my-6">
            <h1 class="title has-text-primary" v-if="!isInvite">Sign up to create your choir space</h1>
            <h1 class="title has-text-primary" v-if="isInvite">Sign up to join {{ choir?.name }}'s space</h1>

            <p v-if="state == State.InviteNotFound">Invite not found. Make sure you entered the correct URL, and check
                if the invite is still active.</p>
        </section>

        <div v-if="state == State.NewRegistration || state == State.InviteLoaded || state == State.Saving">
            <div class="field">
                <label class="label">Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Elvis" v-model="displayName" />
                </div>
            </div>

            <div class="field" v-if="!isInvite">
                <label class="label">Name of your choir</label>
                <div class="control">
                    <input class="input" type="text" placeholder="" v-model="choirName" />
                </div>
            </div>

            <div class="field">
                <label class="label">Email</label>
                <div class="control has-icons-left">
                    <input class="input" type="email" placeholder="you@" v-model="email" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-envelope"></i>
                    </span>
                </div>
            </div>

            <div class="field">
                <label class="label">Password</label>
                <p class="control has-icons-left">
                    <input class="input" type="password" placeholder="Super safe password" v-model="password" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-lock"></i>
                    </span>
                </p>
            </div>

            <div class="control">
                <button class="button is-primary" @click="submit" :class="{ 'is-loading': state == State.Saving }">
                    Create account
                </button>
            </div>
        </div>

        <div v-if="state == State.Finished" class="has-text-success">Account successfully created. You can now login your username <b>{{ email }}</b> and your password.</div>

        <div v-if="state == State.Failed" class="has-text-danger">
            Failed to save user:<br>
            {{ errorMessage }}
        </div>
    </div>
</template>

<script setup lang="ts">
import { inject, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { Choir } from "@/entities/choir";
import { ApiKey } from "@/application/api";

enum State {
    Loading,
    InviteNotFound,
    InviteLoaded,
    NewRegistration,
    Saving,
    Finished,
    Failed
}

// State
const route = useRoute();
const api = inject(ApiKey)!;

const state = ref<State>(State.Loading);
const token = route.query.invite as string;
const isInvite = !!token;
const displayName = ref<string>();
const email = ref<string>();
const password = ref<string>();
const choirName = ref<string>();

const choir = ref<Choir>();
const errorMessage = ref<string>();

onMounted(() => {
    if (token) {
        api.getInviteByToken(token)
            .then((invite) => {
                email.value = invite.email;
                choir.value = invite.choir;
                state.value = State.InviteLoaded;
            })
            .catch((error) => {
                console.log(error);
                
                errorMessage.value = "Failed to find invitation";
                state.value = State.InviteNotFound;
            });
    } else {
        state.value = State.NewRegistration;
    }
});

const submit = () => {
    if (displayName.value == undefined || displayName.value === "") {
        errorMessage.value = "Please enter a valid display name";
        return;
    }
    if (email.value == undefined || email.value === "") {
        errorMessage.value = "Please enter a valid email";
        return;
    }
    if (password.value == undefined || password.value === "") {
        errorMessage.value = "Please enter a valid password";
        return;
    }
    if (!isInvite && (choirName.value == undefined || choirName.value==="")) {
        errorMessage.value = "Please enter a valid choir name";
        return;
    }

    state.value = State.Saving;
    errorMessage.value = undefined;
    let promise;
    if (isInvite) promise = api.acceptInvite(token, displayName.value, email.value, password.value);
    else promise = api.register(choirName.value!, displayName.value, email.value, password.value);
    promise
        .then((response) => {
            console.log(response);
            state.value = State.Finished;
        })
        .catch((error) => {
            errorMessage.value =
                error.response.data.message ?? "Error while sending request: " + error.response.statusText;
                State.Failed;
        });
};
</script>
