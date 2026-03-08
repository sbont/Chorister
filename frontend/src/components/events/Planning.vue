<template>
    <div class="columns is-gapless">
        <div id="menu" class="column is-hidden-mobile	is-one-third-tablet is-one-fifth-desktop has-background-grey-darker">
            <aside class="menu p-2">
                <p v-if="authStore.userCan('create', 'event')" class="menu-label">Create</p>
                <ul v-if="authStore.userCan('create', 'event')" class="menu-list">
                    <li>
                        <router-link :to="{ name: 'NewEvent' }">
                            <span class="icon">
                                <i class="fas fa-plus-square"></i>
                            </span>
                            Add event
                        </router-link>
                    </li>
                </ul>

                <p class="menu-label">Upcoming</p>
                <ul class="menu-list">
                    <li v-if="isLoading">
                        <span class="icon-text">
                            <span class="icon">
                                <i class="fas fa-spinner fa-pulse"></i>
                            </span>
                            <span>Loading...</span>
                        </span>
                    </li>
                    <li v-for="event in futureEvents" v-else :key="event.id">
                        <router-link :to="{ name: 'Event', params: { id: event.id } }">
                            {{ event.name }}
                        </router-link>
                    </li>
                </ul>

                <p class="menu-label">Past</p>
                <ul class="menu-list">
                    <li v-if="isLoading">
                        <span class="icon-text">
                            <span class="icon">
                                <i class="fas fa-spinner fa-pulse"></i>
                            </span>
                            <span>Loading...</span>
                        </span>
                    </li>
                    <li v-for="event in pastEvents" v-else :key="event.id">
                        <router-link :to="{ name: 'Event', params: { id: event.id } }">
                            {{ event.name }}
                        </router-link>
                    </li>
                </ul>
            </aside>
        </div>
        <div class="mobile-menu is-hidden-tablet">
          <div class="column">
            <Select v-model="selectedEvent" :options="options" option-group-label="label" option-group-children="items" option-label="label" option-value="value" placeholder="Select an event" class="w-full md:w-56" @change="selectEvent" />
          </div>
        </div>
        <div class="column">
            <!-- this construction is necessary to rerender between different routes using the same view -->
            <router-view v-slot="{ Component, route }">
                <component :is="Component" :key="route.path" />
            </router-view>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { useAuth } from "@/application/authStore";
import Select, { SelectChangeEvent } from 'primevue/select';
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

const authStore = useAuth();
const eventStore = useEvents();
eventStore.initialize();
const router = useRouter();

// State
const selectedEvent = ref<number>();
const { futureEvents, pastEvents, isLoading } = storeToRefs(eventStore);
const options = computed(() => ([{
  label: "Future events",
  items: futureEvents.value.map(e => ({ "label": e.name, "value": e.id }))
}, {
  label: "Past events",
  items: pastEvents.value.map(e => ({ "label": e.name, "value": e.id }))
}]));

function selectEvent(evt: SelectChangeEvent) {
  router.push({ name: 'Event', params: { id: evt.value } })
}

</script>
