<template>
    <div v-if="isLoading" class="p-3">
        <span class="icon-text">
            <span class="icon">
                <i class="fas fa-spinner fa-pulse"></i>
            </span>
            <span>Loading...</span>
        </span>
    </div>
    <div v-else-if="state == State.NotFound" class="p-3">
        <h3>
            No upcoming events planned.
            <router-link v-if="authStore.userCan('create', 'event')" :to="{ name: 'NewEvent' }">
                <span class="icon">
                    <i class="fas fa-plus-square"></i>
                </span>
                Create new event
            </router-link>
        </h3>
    </div>
</template>

<script setup lang="ts">
import { useAuth } from "@/application/authStore";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { computed, watch } from "vue";
import { useRouter } from "vue-router";

// State
enum State {
    Loading,
    NotFound,
    Redirecting
}

const router = useRouter();
const authStore = useAuth();
const eventStore = useEvents();

const { futureEvents, isLoading } = storeToRefs(eventStore);
const state = computed(() => isLoading.value ? State.Loading : (futureEvents.value.length ? State.Redirecting : State.NotFound));

eventStore.initialize().then(() => redirectIfReady(isLoading.value));

watch(isLoading, loading => {
    redirectIfReady(loading);
});

function redirectIfReady(loading: boolean) {
    if (!loading) {
        const upcoming = futureEvents.value[0];
        if (upcoming) {
            router.push({ name: "Event", params: { id: upcoming.id } });
        } 
    }
}

</script>