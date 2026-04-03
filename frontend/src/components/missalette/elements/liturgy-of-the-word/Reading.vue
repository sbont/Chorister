<!-- eslint-disable vue/no-v-html -->
<template>
  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td>
          <h2>{{ header }}</h2>
        </td>
        <td><h4 v-html="reading.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="reading">
    <p v-html="reading.text"></p>
  </div>

  <div class="dialogue">
    <p>The word of the Lord.</p>
    <blockquote>Thanks be to God.</blockquote>
  </div>
</template>

<script setup lang="ts">
import { Reading } from "@/entities/reading";

defineProps<{
  header: string;
  reading: Reading;
}>();
</script>

<style lang="scss" scoped>
@import "../../missalette.scss";
</style>
