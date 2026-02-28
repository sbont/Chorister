<template>
    <div v-if="state != State.Loading" class="signup container">
        <section class="my-6">
            <h1 v-if="!isInvite" class="title has-text-primary">Sign up to create your choir space</h1>
            <h1 v-if="isInvite" class="title has-text-primary">Sign up to join {{ choir?.name }}'s space</h1>

            <p v-if="state == State.InviteNotFound">Invite not found. Make sure you entered the correct URL, and check
                if the invite is still active.</p>
        </section>

        <div v-if="state == State.NewRegistration || state == State.InviteLoaded || state == State.Saving">
            <div class="field">
                <label class="label">First name</label>
                <div class="control">
                    <input v-model="firstName" class="input" type="text" placeholder="John" />
                </div>
            </div>

            <div class="field">
                <label class="label">Last name</label>
                <div class="control">
                    <input v-model="lastName" class="input" type="text" placeholder="Mayer" />
                </div>
            </div>

            <div v-if="!isInvite" class="field">
                <label class="label">Name of your choir</label>
                <div class="control">
                    <input v-model="choirName" class="input" type="text" placeholder="" />
                </div>
            </div>

            <div class="field">
                <label class="label">Email</label>
                <div class="control has-icons-left">
                    <input v-model="email" class="input" type="email" placeholder="you@" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-envelope"></i>
                    </span>
                </div>
            </div>

            <div class="field">
                <label class="label">Password</label>
                <p class="control has-icons-left">
                    <input v-model="password" class="input" type="password" placeholder="Super safe password" />
                    <span class="icon is-small is-left">
                        <i class="fas fa-lock"></i>
                    </span>
                </p>
            </div>

            <div class="control">
                <button class="button is-primary" :class="{ 'is-loading': state == State.Saving }" @click="submit">
                    Create account
                </button>
            </div>
        </div>

        <div v-if="state == State.Finished" class="has-text-success">Account successfully created. You can now login with
            your username <b>{{ email }}</b> and your password.</div>

        <div v-if="errorMessage" class="has-text-danger">
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
import { ApiError } from "@/types/api-error";

enum State {
    Loading,
    InviteNotFound,
    InviteLoaded,
    NewRegistration,
    Saving,
    Finished,
}

// State
const route = useRoute();
const api = inject(ApiKey);

const state = ref<State>(State.Loading);
const token = route.query.invite as string;
const isInvite = !!token;
const firstName = ref<string>();
const lastName = ref<string>();
const email = ref<string>();
const password = ref<string>();
const choirName = ref<string>();

const choir = ref<Choir>();
const errorMessage = ref<string>();

onMounted(() => {
  if (!api) {
    throw new Error('Backend client not injected');
  }
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

async function submit() {
  if (!api) {
    throw new Error('Backend client not injected');
  }

  const previousState = state.value;

  if (firstName.value == undefined || firstName.value === "") {
      errorMessage.value = "Please enter a valid first name";
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

  state.value = State.Saving;
  errorMessage.value = undefined;

  try {
    if (isInvite) {
      await api.acceptInvite(token, firstName.value, lastName.value, email.value, password.value);
    } else { 
      if (choirName.value == undefined || choirName.value === "") {
        errorMessage.value = "Please enter a valid choir name";
        return;
      }

      await api.register(choirName.value, firstName.value, lastName.value, email.value, password.value);
    }

    state.value = State.Finished;
  } catch(e) {
    console.log(e);
    errorMessage.value = (e as ApiError).message;
    state.value = previousState;
  }
}
  
</script>
