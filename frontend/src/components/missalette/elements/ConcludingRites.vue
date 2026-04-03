<template>
  <h2>Prayer after Communion</h2>

  <div class="collect">
    <p>Let us pray.</p>
    <pre>
All pray in silence. Then the Priest says the Prayer after Communion, at the end of which the people acclaim:</pre
    >
    <blockquote>Amen.</blockquote>
  </div>

  <div class="concluding-rites">
    <h1>The Concluding Rites</h1>

    <slot name="blessing">
      <div class="dialogue">
        <p>The Lord be with you.</p>
        <blockquote>And with your spirit.</blockquote>
        <p>May almighty God bless you, the Father, and the Son, and the Holy Spirit.</p>
        <blockquote>Amen.</blockquote>
        <p>Go forth, the Mass is ended.</p>
        <blockquote>Thanks be to God.</blockquote>
      </div>
    </slot>
  </div>

  <Song heading="Recessional hymn" :title="recessionalHymn" />
</template>

<script setup lang="ts">
import Song from "./Song.vue";
defineProps<{
  recessionalHymn: string;
}>();
</script>

<style lang="scss" scoped>
@import "../missalette.scss";
</style>
