<template>
    <div class="m-2 p-3">
        <div class="is-size-4">Chords</div>
        <div>{{ error }}</div>
        <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
            <ChordsComponent v-for="chords in chordses" :key="chords.id" :value="(chords as Chords)"
                @remove="removeChords(chords)"></ChordsComponent>
            <div v-if="!draftValues">
                <button class="button is-primary" @click="addChords">
                    Add
                </button>
            </div>
            <div v-else>
                <ChordsComponent :value="draftValues" @cancel="cancelAdd" @added="onAdded"></ChordsComponent>
            </div>

        </div>
    </div>
</template>

<script setup lang="ts">
import { useChords } from "@/application/chordsStore";
import ChordsComponent from "@/components/Chords.vue";
import { Chords } from "@/entities/chords";
import { EntityRef, toEntityRef } from "@/entities/entity";
import { Song } from "@/entities/song";
import { isNew } from "@/utils";
import { ref } from "vue";

type DraftChords = Partial<Chords> & {
    song: EntityRef<Song>
}

const props = defineProps<{
    song: Song
}>();

const loading = ref(true)
const error = ref<string | undefined>(undefined)
const chordsStore = useChords();
const chordses = ref<Array<Chords>>([])
const draftValues = ref<DraftChords | undefined>(undefined)

if (props.song.chords)
    chordsStore.getAllRelated(props.song.chords)
        .then(data => chordses.value = data)
        .catch(e => error.value = e)
        .finally(() => loading.value = false);;


const addChords = () => draftValues.value = { song: toEntityRef(props.song) }
const cancelAdd = () => draftValues.value = undefined

const onAdded = (chords: Chords) => {
    chordses.value.push(chords)
    draftValues.value = undefined
}

const removeChords = (chords: Chords) => {
    if (!isNew(chords)) {
        chordsStore.delete(chords);
    }
    chordses.value = chordses.value.filter(current => current.id !== chords.id);
}

</script>

<style scoped></style>