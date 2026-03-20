<template>
  <div class="mass-order">
    <OpeningPrayer />
    <LiturgyOfTheWord v-if="event" :event="event" omit-creed omit-prayers-of-the-faithful />
    <SolemnIntercessions />
    <Adoration />
    

  </div>
</template>

<script setup lang="ts">
import { useEvents } from '@/application/eventStore';
import { useRoute } from 'vue-router';
import { storeToRefs } from 'pinia';
import { ref, computed } from 'vue';
import { Event } from '@/entities/event';
import LiturgyOfTheWord from './elements/LiturgyOfTheWord.vue';
import Song from './elements/Song.vue';
import LiturgyOfTheEucharist from './elements/LiturgyOfTheEucharist.vue';
import ConcludingRites from './elements/maundy-thursday/ConcludingRites.vue';
import OpeningPrayer from './elements/good-friday/OpeningPrayer.vue';
import SolemnIntercessions from './elements/good-friday/SolemnIntercessions.vue';
import Adoration from './elements/good-friday/Adoration.vue';

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