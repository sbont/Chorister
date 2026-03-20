<template>
  <h1>The Liturgy of the Word</h1>
  <Readings :event="event" :show-copyright="false" />
  <h2>The Homily</h2>
  <Creed v-if="omitCreed !== true" />

  <slot name="postHomily"/>

  <PrayerOfTheFaithful v-if="omitPrayersOfTheFaithful !== true" />
</template>

<script setup lang="ts">
import Readings from './liturgy-of-the-word/Readings.vue';
import Creed from './liturgy-of-the-word/Creed.vue';
import PrayerOfTheFaithful from './liturgy-of-the-word/PrayerOfTheFaithful.vue';
import { Event } from '@/entities/event';

defineProps<{
  event: Event;
  omitCreed?: boolean;
  omitPrayersOfTheFaithful?: boolean;
}>();

</script>

<style lang="scss" scoped></style>