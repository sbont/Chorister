<template>
    <div class="profile" v-if="!loading">
        <section class="hero is-medium is-link">
            <div class="hero-body">
                <p class="subtitle">hello</p>
                <p class="title">{{ user.displayName }}</p>
            </div>
        </section>

        <div class="pt-5">
            <section class="section">
            <h1 class="title">User details</h1>
            <div class="w-40">
                <div class="field">
                    <label class="label">Name</label>
                    <div class="control">
                        <input class="input" type="text" v-model="draftValues.displayName" />
                    </div>
                </div>

                <div class="field">
                    <label class="label">Email</label>
                    <div class="control has-icons-left">
                        <input class="input" type="email" placeholder="you@" v-model="draftValues.email" />
                        <span class="icon is-small is-left">
                            <i class="fas fa-envelope"></i>
                        </span>
                    </div>
                </div>

                <button
                    @click="save"
                    class="button is-link"
                    :class="{ 'is-loading': saving }"
                >
                    Save changes
                </button>
            </div>
            </section>
        </div>
    </div>
</template>

<script>
import { useAuth } from "@/stores/authStore";
import { useUsers } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import { computed, onMounted, ref } from "vue";

export default {
    setup() {
        const auth = useAuth();
        const userStore = useUsers();
        const { user: authUser } = storeToRefs(auth);

        // State
        const user = ref()
        const loading = ref(true)
        const saving = ref(false)
        const draftValues = ref({})

        // Computed
        onMounted(() => {
            console.log(auth.getUserZitadelId());
            console.log(userStore.getCurrent());
            userStore.getCurrent()
                .then((value) => {
                    user.value = value;
                    draftValues.value = value;
                })
                .finally(() => loading.value = false);
        });

        // Methods
        const save = () => {
            saving.value = true;
            user.value = draftValues.value;
            userStore.save(draftValues.value)
                .finally(() => saving.value = false);
        }

        return {
            auth, user, draftValues, loading, saving,
            save
        }
    }
}
</script>
<style>
.w-40 {
    max-width: 40rem;
}
</style>