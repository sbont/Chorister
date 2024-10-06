<template>
    <div class="m-2 p-3">
        <div class="is-size-4">Chords</div>
        <div>{{ error }}</div>
        <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
            <ChordsComponent v-for="chords in chordses" :key="chords._links?.self.href" :value="(chords as Chords)"
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
import ChordsComponent from "@/components/Chords.vue"
import { Chords, DraftChords } from "@/types";
import { ref } from "vue";
import { isNew } from "@/utils";
import { useChords } from "@/stores/chordsStore";


const props = defineProps({
    songUri: String
})

const loading = ref(true)
const error = ref<string | undefined>(undefined)
const chordsStore = useChords()
const chordses = ref<Array<Chords>>([]);
chordsStore.fetchRelated(props.songUri as string, "chords").then(data => chordses.value = data as Chords[]).catch(e => error.value = e).finally(() => loading.value = false)
const draftValues = ref<DraftChords | undefined>(undefined)

const addChords = () => draftValues.value = {song: props.songUri}
const cancelAdd = () => draftValues.value = undefined

const onAdded = (chords: Chords) => {
    chordses.value.push(chords)
    draftValues.value = undefined
}

const removeChords = (chords: Chords) => {
    if (!isNew(chords)) {
        chordsStore.delete(chords);
    }
    chordses.value = chordses.value.filter(current => current._links?.self.href !== chords._links?.self.href);
}

</script>

<style scoped></style>