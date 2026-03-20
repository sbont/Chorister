<template>
  <h1>The Introductory Rites</h1>
  <Song heading="Entrance hymn" :title="entranceHymn" />    
  <Greeting />
  <PenitentialAct />
  <Collect />
</template>

<script setup lang="ts">
import Song from './Song.vue';
import Greeting from './introductory-rites/Greeting.vue';
import PenitentialAct from './introductory-rites/PenitentialAct.vue';
import Collect from './introductory-rites/Collect.vue';

defineProps<{
  entranceHymn: string
}>();

</script>

<style lang="scss" scoped></style>