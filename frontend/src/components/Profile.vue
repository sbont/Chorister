<template>
    <div class="profile" v-if="!loading && user">
        <DetailHeader :title="user?.firstName" subtitle="hello" subtitle-on-top />

        <div class="pt-5">
            <section class="section">
                <h1 class="title">User details</h1>
                <div class="w-40">
                    <div class="field">
                        <label class="label">First name</label>
                        <div class="control">
                            <input class="input" type="text" v-if="draftValues" v-model="draftValues.firstName" />
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Last name</label>
                        <div class="control">
                            <input class="input" type="text" v-if="draftValues" v-model="draftValues.lastName" />
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Email</label>
                        <div class="control has-icons-left">
                            <input class="input" type="email" placeholder="you@" v-if="draftValues"
                                v-model="draftValues.email" />
                            <span class="icon is-small is-left">
                                <i class="fas fa-envelope"></i>
                            </span>
                        </div>
                    </div>

                    <button @click="save" class="button is-link" :class="{ 'is-loading': saving }">Save changes</button>
                </div>
            </section>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useUsers } from "@/application/userStore";
import { onMounted, ref } from "vue";
import { User } from "@/entities/user";
import DetailHeader from "./ui/DetailHeader.vue";

const userStore = useUsers();

// State
const user = ref<User>();
const loading = ref(true);
const saving = ref(false);
const draftValues = ref<User>();

// Computed
onMounted(() => {
    userStore
        .getCurrent()
        .then((value) => {
            user.value = value;
            draftValues.value = value;
        })
        .finally(() => (loading.value = false));
});

// Methods
const save = () => {
    saving.value = true;
    user.value = draftValues.value;
    if (draftValues.value) userStore.save(draftValues.value).finally(() => (saving.value = false));
};
</script>
<style>
.w-40 {
    max-width: 40rem;
}
</style>