<template>
  <h1>The Liturgy of the Word</h1>
  <Readings :event="event" :show-copyright="false" />
  <h2>The Homily</h2>
  <Creed />
  <PrayerOfTheFaithful />
</template>

<script setup lang="ts">
import Readings from './liturgy-of-the-word/Readings.vue';
import Creed from './liturgy-of-the-word/Creed.vue';
import PrayerOfTheFaithful from './liturgy-of-the-word/PrayerOfTheFaithful.vue';
import { Event } from '@/entities/event';

defineProps<{
  event: Event;
}>();

</script>

<style lang="scss" scoped></style>