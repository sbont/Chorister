<template>
    <div class="text">
        <div v-for="entry in entries" :key="entry.id">
            <div v-if="entry.label"><b>{{ entry.label }}</b></div>
            <div v-if="entry.song"><b>{{ entry.song.embedded?.title }}</b></div>
            <div v-if="entry.song" v-html="entry.song.embedded?.text"></div>
            <p></p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { computed, ref } from "vue";
import { Event } from "@/entities/event";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";

const eventStore = useEvents();
const route = useRoute();

// state
const eventId = Number(route.params.id);
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const entries = computed(() => event.value ? getEntries.value(event.value.uri!) : []);
eventStore.fetch(eventId).then((result) => {
    event.value = result;
});

</script>

<style>
.text p {
    margin-top: 12px;
    min-height: 18px;
}
</style>