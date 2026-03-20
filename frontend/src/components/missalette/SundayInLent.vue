<template>
  <div class="mass-order">
    <IntroductoryRites :entrance-hymn="songByIndex(1)" :omit-gloria="true"/>
    <LiturgyOfTheWord v-if="event" :event="event" />
    <LiturgyOfTheEucharist
      :offertory-hymn="songByIndex(3)"
      :communion-hymn="songByIndex(6)"
      :thanksgiving-hymn="songByIndex(7)" 
    />
    <ConcludingRites :recessional-hymn="songByIndex(8)" />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { computed, ref } from "vue";
import { Event } from "@/entities/event";
import { useEvents } from "@/application/eventStore";
import { storeToRefs } from "pinia";
import ConcludingRites from "./elements/ConcludingRites.vue";
import IntroductoryRites from "./elements/IntroductoryRites.vue";
import LiturgyOfTheWord from "./elements/LiturgyOfTheWord.vue";
import LiturgyOfTheEucharist from "./elements/LiturgyOfTheEucharist.vue";

const eventStore = useEvents();
const route = useRoute();

// state
const eventId = Number(route.params.id);
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const entries = computed(() => event.value?.uri ? getEntries.value(event.value.uri) : []);
eventStore.fetch(eventId).then(async (result) => {
    event.value = result;
});
const songByIndex = computed(() => (index: number) => {
  const song = entries.value[index - 1]?.song?.embedded;
  return song ? `${song.songbookNumber ? song.songbookNumber + '. ' : ''}${song.title}` : '';
});

</script>

<style>
@import "./missalette.scss";

</style>