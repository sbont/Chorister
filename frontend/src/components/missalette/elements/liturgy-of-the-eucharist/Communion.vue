<template>
  <h1>The Communion Rite</h1>

  <h2>The Lord's Prayer</h2>

  <div class="dialogue">
    <p>At the Saviour's command and formed by divine teaching, we dare to say:</p>
    <blockquote>Our Father, who art in heaven,<br>
      hallowed be thy name;<br>
      thy kingdom come,<br>
      thy will be done on earth as it is in heaven.<br>
      Give us this day our daily bread,<br>
      and forgive us our trespasses,<br>
      as we forgive those who trespass against us;<br>
      and lead us not into temptation,<br>
      but deliver us from evil.</blockquote>
  </div>

  <div class="dialogue">
    <p>Deliver us, Lord, we pray, from every evil…<br>
      and the coming of our Saviour, Jesus Christ.</p>
    <blockquote>For the kingdom, the power and the glory are yours now and for ever.</blockquote>
  </div>

  <Peace v-if="omitPeace !== true"/>
  <BreakingOfBread v-if="omitBreakingOfBread !== true" />

  <h2>Communion</h2>

  <div class="dialogue">
    <p>Behold the Lamb of God, behold him who takes away the sins of the world.<br>
      Blessed are those called to the supper of the Lamb.</p>
    <blockquote>Lord, I am not worthy that you should enter under my roof, but only say the word and my soul shall be healed.</blockquote>
  </div>

  <div class="dialogue">
    <pre>The communicants come forward in reverent procession, and make a preparatory act of reverence by bowing their head in honour of Christ's presence in the Sacrament.</pre>
    <p>The Body of Christ. <strong>Amen.</strong></p>
  </div>
</template>

<script setup lang="ts">
import BreakingOfBread from './BreakingOfBread.vue';
import Peace from './Peace.vue';

defineProps<{
  omitPeace?: boolean;
  omitBreakingOfBread?: boolean;
}>();
</script>

<style lang="scss" scoped>
@import "../../missalette.scss";
</style>