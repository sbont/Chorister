<template>
    <div v-if="state == State.NotFound" class="p-3">
        <h3>
            No upcoming events planned.
            <router-link :to="{ name: 'NewEvent' }" append>
                <span class="icon">
                    <i class="fas fa-plus-square"></i>
                </span>
                Create new event
            </router-link>
        </h3>
    </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useRouter } from "vue-router";

// State
enum State {
    Loading,
    NotFound,
    Redirecting
}

const state = ref<State>(State.Loading);
const router = useRouter();
const eventStore = useEvents();
const { futureEvents } = storeToRefs(eventStore);

function setUpcoming() {
    const upcoming = futureEvents.value[0];
    if (upcoming) {
        state.value = State.Redirecting;
        router.push({ name: "Event", params: { id: upcoming.id }});
    } else {
        state.value = State.NotFound;
    }
}

if (!futureEvents.value.length)
    eventStore.fetchAll().then(_ => {
        setUpcoming();
    })
else
    setUpcoming();

</script>