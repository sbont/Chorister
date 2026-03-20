<template>
  <h1>The Commemoration of the Lord’s Entrance into Jerusalem</h1>

    <Greeting />
    
    <div class="dialogue">
      <p>
        Dear brothers and sisters,
        since the beginning of Lent until now
        we have prepared our hearts by penance and charitable works.
        Today we gather together to herald with the whole Church
        the beginning of the celebration
        of our Lord’s Paschal Mystery,
        that is to say, of his Passion and Resurrection.
        For it was to accomplish this mystery
        that he entered his own city of Jerusalem.
        Therefore, with all faith and devotion,
        let us commemorate
        the Lord’s entry into the city for our salvation,
        following in his footsteps,
        so that, being made by his grace partakers of the Cross,
        we may have a share also in his Resurrection and in his life.
      </p>
      
      <p>Let us pray.</p>
      <p>
        Almighty ever-living God,
        sanctify these branches with your blessing,
        that we, who follow Christ the King in exultation,
        may reach the eternal Jerusalem through him.
        Who lives and reigns for ever and ever.<br>

        Or:
        <br>
        Increase the faith of those who place their hope in you, O God,
        and graciously hear the prayers of those who call on you,
        that we, who today hold high these branches
        to hail Christ in his triumph,
        may bear fruit for you by good works accomplished in him.
        Who lives and reigns for ever and ever.
      </p>
      <blockquote>Amen.</blockquote>
    </div>

    <EntranceGospelA />

    <div class="dialogue">
      <p>Dear brothers and sisters,<br> 
        like the crowds who acclaimed Jesus in Jerusalem,
        let us go forth in peace.
      </p>
      <blockquote>Let us go forth in peace.</blockquote>

      or

      <p>Let us go forth in peace.</p>
      <blockquote>In the name of Christ. Amen.</blockquote>
    </div>

    <Song heading="Procession hymn" :title="processionHymn" />
    <Collect />
</template>

<script setup lang="ts">
import Collect from '../introductory-rites/Collect.vue';
import Greeting from '../introductory-rites/Greeting.vue';
import Song from '../Song.vue';
import EntranceGospelA from './EntranceGospelA.vue';

defineProps<{
  processionHymn: string
}>();

</script>

<style lang="scss" scoped>
@import "../../missalette.scss";
</style>
