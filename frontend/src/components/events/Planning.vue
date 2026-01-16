<template>
    <div class="columns is-gapless">
        <div class="column is-one-third-tablet is-one-fifth-desktop has-background-grey-darker" id="menu">
            <aside class="menu p-2">
                <p class="menu-label" v-if="authStore.userCan('create', 'event')">Create</p>
                <ul class="menu-list" v-if="authStore.userCan('create', 'event')">
                    <li>
                        <router-link :to="{ name: 'NewEvent' }" append>
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
                    <li v-else v-for="event in futureEvents" :key="event.id">
                        <router-link :to="{ name: 'Event', params: { id: event.id } }" append>
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
                    <li v-else v-for="event in pastEvents" :key="event.id">
                        <router-link :to="{ name: 'Event', params: { id: event.id } }" append>
                            {{ event.name }}
                        </router-link>
                    </li>
                </ul>
            </aside>
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
import { computed } from "vue";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import { useAuth } from "@/application/authStore";

const authStore = useAuth();
const eventStore = useEvents();
eventStore.initialize();

// State
const { futureEvents, pastEvents, isLoading } = storeToRefs(eventStore);

// Computed
const ready = computed(() => !!futureEvents);


</script>
