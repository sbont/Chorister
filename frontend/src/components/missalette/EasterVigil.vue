<template>
  <div class="mass-order">
    <Lucernarium />
    <Exsultet />
    <LiturgyOfTheWord
      v-if="event"
      :event="event"
      omit-creed
      omit-prayers-of-the-faithful
      is-easter
    />

    <SolemnIntercessions />
    <Adoration>
      <template #venerationHymns>
        <Song heading="Hymn" :title="songByIndex(1)" />
        <Song heading="Hymn" :title="songByIndex(2)" />
      </template>
    </Adoration>
    <Communion omit-peace omit-breaking-of-bread />

    <Song heading="Communion hymn" :title="songByIndex(3)" />
    <Song heading="Thanksgiving hymn" :title="songByIndex(4)" />

    <ConcludingRites />
  </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { Event } from "@/entities/event";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute } from "vue-router";
import LiturgyOfTheWord from "./elements/LiturgyOfTheWord.vue";
import Song from "./elements/Song.vue";
import Exsultet from "./elements/easter-vigil/Exsultet.vue";
import Lucernarium from "./elements/easter-vigil/Lucernarium.vue";
import Adoration from "./elements/good-friday/Adoration.vue";
import ConcludingRites from "./elements/good-friday/ConcludingRites.vue";
import SolemnIntercessions from "./elements/good-friday/SolemnIntercessions.vue";
import Communion from "./elements/liturgy-of-the-eucharist/Communion.vue";

const eventStore = useEvents();
const route = useRoute();

// state
const eventId = Number(route.params.id);
const { entries: getEntries } = storeToRefs(eventStore);
const event = ref<Event>();
const entries = computed(() =>
  event.value?.uri ? getEntries.value(event.value.uri) : []
);
eventStore.fetch(eventId).then(async (result) => {
  event.value = result;
});
const songByIndex = computed(() => (index: number) => {
  const song = entries.value[index - 1]?.song?.embedded;
  return song
    ? `${song.songbookNumber ? song.songbookNumber + ". " : ""}${song.title}`
    : "";
});
</script>

<style>
@import "./missalette.scss";
</style>
