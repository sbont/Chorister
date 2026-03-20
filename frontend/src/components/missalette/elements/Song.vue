<template>
  <div class="song">
    <h5>{{ heading }}</h5>
    <h6>{{ title }}</h6>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  heading: string;
  title: string;
}>();
</script>
