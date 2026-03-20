<!-- eslint-disable vue/no-v-html -->
<template>
  <div class="mass-order">
    <IntroductoryRites :entrance-hymn="songByIndex(1)" :omit-penitential-act="true" :omit-gloria="true" />
    <LiturgyOfTheWord v-if="event" :event="event" :omit-creed="true" >
      <template #postHomily>
        <h2>Blessing of Ashes</h2>

        <h2>Distribution of Ashes</h2>

        <div class="distribution-of-ashes">
          <p><em>The Priest places ashes on the head of all those present who come to him, and says to each one:</em></p>
          <p>Repent, and believe in the Gospel.<br>
            Or:<br>
            Remember that you are dust, and to dust you shall return.</p>
        </div>

        <Song heading="Ash Wednesday hymn" :title="songByIndex(2)"/>
      </template>
    </LiturgyOfTheWord>
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
import IntroductoryRites from "./elements/IntroductoryRites.vue";
import LiturgyOfTheWord from "./elements/LiturgyOfTheWord.vue";
import Song from "./elements/Song.vue";
import LiturgyOfTheEucharist from "./elements/LiturgyOfTheEucharist.vue";
import ConcludingRites from "./elements/ConcludingRites.vue";

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

.distribution-of-ashes, .prayer-of-faithful {
  margin: 1em 0;
}

</style>