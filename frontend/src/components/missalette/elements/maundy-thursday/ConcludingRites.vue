<template>
  <h2>Prayer after Communion</h2>

  <div class="collect">
    <p>Let us pray.</p>
    <p>
      Grant, almighty God, that, just as we are renewed by the Supper of your Son in this present age, so we may enjoy his banquet for all eternity. 
      Who lives and reigns for ever and ever.
    </p>
    <blockquote>Amen.</blockquote>
  </div>
  
  <h1>The Transfer of the Most Blessed Sacrament</h1>
  <Song heading="Procession hymn" :title="processionHymn" />
</template>

<script setup lang="ts">
import Song from '../Song.vue';

defineProps<{
  processionHymn: string
}>();

</script>

<style lang="scss" scoped>
@import "../../missalette.scss";

</style>