<!-- eslint-disable vue/no-v-html -->
<template>
  <table cellspacing="0" cellpadding="0" class="reading-header" width="100%">
    <tbody>
      <tr>
        <td><h2>Psalm</h2></td>
        <td><h4 v-html="psalm.source"></h4></td>
      </tr>
    </tbody>
  </table>

  <div class="psalm">
    <div v-html="psalm.text"></div>
  </div>
</template>

<script setup lang="ts">
import { Reading } from "@/entities/reading";

defineProps<{
  psalm: Reading;
}>();
</script>

<style lang="scss" scoped>
@import "../../missalette.scss";
</style>
