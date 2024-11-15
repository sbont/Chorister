<template>
    <div class="signup container" v-if="!loading">
        <section class="mt-6">
            <h1 class="title has-text-primary">Register as a new user</h1>
            <h2 class="subtitle has-text-info" v-if="isInvite">joining {{ choir?.name }}</h2>
        </section>

        <div class="pt-5" v-if="registration">
            <div class="field">
                <label class="label">Name</label>
                <div class="control">
                    <input class="input" type="text" placeholder="Elvis" v-model="registration.displayName" />
                </div>
            </div>

            <div class="field" v-if="!isInvite">
                <label class="label">Name of your choir</label>
                <div class="control">
                    <input class="input" type="text" placeholder=""
                        v-model="(registration as NewChoirRegistration).choirName" />
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
                    <input class="input" type="password" placeholder="Super safe password"
                        v-model="registration.password" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-lock"></i>
                    </span>
                </p>
            </div>

            <div class="control">
                <button class="button is-primary" @click="submit" :class="{ 'is-loading': saving }">
                    Create account
                </button>
            </div>

            <div v-if="isSuccess" class="has-text-success">
                Account created.
            </div>
            <div v-if="errorMessage" class="has-text-danger">
                {{ errorMessage }}
            </div>
        </div>

    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import api from "../services/api.js";
import { AcceptInvite, Choir, NewChoirRegistration, Registration } from "@/types";
import { useRoute, useRouter } from "vue-router";

type DraftRegistration = Partial<Registration>


// State
const route = useRoute();
const loading = ref(true)
const saving = ref(false)
const registration = ref<DraftRegistration>()
const isInvite = ref(false)
const choir = ref<Choir>()
const isSuccess = ref<boolean>()
const errorMessage = ref<string>()

onMounted(() => {
    const token = route.query.invite as string;
    isInvite.value = !!token;
    if (token) {
        api.getInviteByToken(token)
            .then((response) => {
                const inviteDetail = response.data;
                registration.value = {
                    email: inviteDetail.email,
                    token: inviteDetail.token
                } as AcceptInvite
                choir.value = inviteDetail.choir;
                loading.value = false;
            })
            .catch((error) => {
                errorMessage.value = "Failed to find invitation";
                loading.value = false;
            });
    } else {
        registration.value = {} as DraftRegistration
        loading.value = false;
    }
})

const submit = () => {
    saving.value = true;
    errorMessage.value = undefined;
    if (!registration.value) {
        return;
    }
    let promise;
    if (isInvite.value)
        promise = api.acceptInvite(registration.value as AcceptInvite)
    else
        promise = api.register(registration.value as NewChoirRegistration)
    promise
        .then((response) => {
            console.log(response);
            isSuccess.value = true;
        })
        .catch((error) => {
            errorMessage.value = error.response.data.message ?? "Error while sending request: " + error.response.statusText;
        })
        .finally(() => saving.value = false);
}

</script>
