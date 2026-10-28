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
    <Baptism :baptismal-hymn="songByIndex(2)" />
    <LiturgyOfTheEucharist
      :offertory-hymn="songByIndex(3)"
      :communion-hymn="songByIndex(6)"
      :thanksgiving-hymn="songByIndex(7)"
    />

    <ConcludingRites :recessional-hymn="songByIndex(8)">
      <template #blessing>
        <SolemnBlessing />
      </template>
    </ConcludingRites>
  </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { Event } from "@/entities/event";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useRoute } from "vue-router";
import LiturgyOfTheEucharist from "./elements/LiturgyOfTheEucharist.vue";
import LiturgyOfTheWord from "./elements/LiturgyOfTheWord.vue";
import Baptism from "./elements/easter-vigil/Baptism.vue";
import Exsultet from "./elements/easter-vigil/Exsultet.vue";
import Lucernarium from "./elements/easter-vigil/Lucernarium.vue";
import SolemnBlessing from "./elements/easter-vigil/SolemnBlessing.vue";
import ConcludingRites from "./elements/ConcludingRites.vue";

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
