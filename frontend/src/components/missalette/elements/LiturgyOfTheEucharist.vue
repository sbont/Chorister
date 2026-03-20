<template>
  <h1>The Liturgy of the Eucharist</h1>

  <Song heading="Offertory hymn" :title="offertoryHymn" />
  <Eucharist />  
  <Song heading="Communion hymn" :title="communionHymn" />
  <Song heading="Thanksgiving hymn" :title="thanksgivingHymn" />
</template>

<script setup lang="ts">
import Eucharist from './liturgy-of-the-eucharist/Eucharist.vue';
import Song from './Song.vue';

defineProps<{
  offertoryHymn: string,
  communionHymn: string,
  thanksgivingHymn: string,
}>();
</script>

<style lang="scss" scoped></style>