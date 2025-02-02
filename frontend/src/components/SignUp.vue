<template>
    <div class="signup container" v-if="!loading">
        <section class="mt-6">
            <h1 class="title has-text-primary">Register as a new user</h1>
            <h2 class="subtitle has-text-info" v-if="isInvite">joining {{ choir?.name }}</h2>
        </section>

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
            <button class="button is-primary" @click="submit" :class="{ 'is-loading': saving }">Create account</button>
        </div>

        <div v-if="isSuccess" class="has-text-success">Account created.</div>
        <div v-if="errorMessage" class="has-text-danger">
            {{ errorMessage }}
        </div>
    </div>
</template>

<script setup lang="ts">
import { inject, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { Choir } from "@/entities/choir";
import { ApiKey } from "@/application/api";

// State
const route = useRoute();
const api = inject(ApiKey)!;

const loading = ref(true);
const saving = ref(false);
const token = route.query.invite as string;
const isInvite = !!token;
const displayName = ref<string>();
const email = ref<string>();
const password = ref<string>();
const choirName = ref<string>();

const choir = ref<Choir>();
const isSuccess = ref<boolean>();
const errorMessage = ref<string>();

onMounted(() => {
    if (token) {
        api.getInviteByToken(token)
            .then((invite) => {
                email.value = invite.email;
                choir.value = invite.choir;
                loading.value = false;
            })
            .catch((error) => {
                errorMessage.value = "Failed to find invitation";
                loading.value = false;
            });
    } else {
        loading.value = false;
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

    saving.value = true;
    errorMessage.value = undefined;
    let promise;
    if (isInvite) promise = api.acceptInvite(token, displayName.value, email.value, password.value);
    else promise = api.register(choirName.value!, displayName.value, email.value, password.value);
    promise
        .then((response) => {
            console.log(response);
            isSuccess.value = true;
        })
        .catch((error) => {
            errorMessage.value =
                error.response.data.message ?? "Error while sending request: " + error.response.statusText;
        })
        .finally(() => (saving.value = false));
};
</script>
