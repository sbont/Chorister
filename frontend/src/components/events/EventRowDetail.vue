<template>
  <div class="columns" v-if="scores?.length">
    <div class="column header-col is-1 is-size-6 has-text-weight-semibold pr-2">
      Scores
    </div>
    <div class="column is-flex is-flex-direction-row flex-wrap">
      <div v-for="score in scores" class="mx-2">
          <FileLink :file-info-uri="score.file?.fileInfoUri" :description="score.description"
            :file-name="entry.song?.embedded?.title + ' - ' + score.description" class="tag"/>
        </div>
    </div>
  </div>
  <div class="columns" v-if="chordses?.length">
    <div class="column header-col is-1 is-size-6 has-text-weight-semibold pr-2">Chords</div>
    <Accordion value="0" class="column is-four-fifth ml-4" >
      <AccordionPanel v-for="chords in chordses" :value="chords.uri">
        <AccordionHeader>{{ chords.key }}</AccordionHeader>
        <AccordionContent>
          <div class="content mono" v-html="chords.chords"></div>
        </AccordionContent>
      </AccordionPanel>
    </Accordion>
  </div>
  

</template>

<script setup lang="ts">
import { useChords } from '@/application/chordsStore';
import { useScores } from '@/application/scoreStore';
import { EventEntry } from '@/entities/event';
import { Score } from '@/entities/score';
import { PropType, ref } from 'vue';
import FileLink from '../songs/FileLink.vue';
import { Chords } from '@/entities/chords';
import Accordion from 'primevue/accordion';
import AccordionPanel from 'primevue/accordionpanel';
import AccordionHeader from 'primevue/accordionheader';
import AccordionContent from 'primevue/accordioncontent';


const props = defineProps({
  entry: {
    type: Object as PropType<EventEntry>,
    required: true
  }
});
const expandedChords = ref<number>();

const scoreStore = useScores();
const chordsStore = useChords();

const scores = ref<Score[] | undefined>();
const chordses = ref<Chords[] | undefined>();
const song = props.entry.song?.embedded;
if (song) {
  if (song.scores)
    scoreStore.getAllRelated(song.scores).then(result => scores.value = result);
  if (song.chords)
    chordsStore.getAllRelated(song.chords).then(result => chordses.value = result);
}


</script>

<style>
.content.mono {
  font-family: 'Fira Mono', Courier, monospace;
  font-size: 10pt;
  white-space: pre-wrap;
}

.header-col {
  width: 2rem;;
}

.column.p-accordion {
  padding: 0 0 0.25rem;
  --p-accordion-header-padding: 1rem;
}

</style>