<template>
    <div class="scores m-2 p-3">
        <div class="is-size-4">Scores</div>
        <div v-if="error" class="is-danger">{{ error }}</div>

        <table class="table is-hoverable is-fullwidth scores-table" v-cloak>
            <thead>
                <tr>
                    <th class="col-no" title="number"></th>
                    <th class="col-name">Description</th>
                    <th class="col-key">Key</th>
                    <th class="col-action"></th>
                    <th class="col-action"></th>
                </tr>
            </thead>
            <tbody>
                <ScoreCopy v-for="(score, index) in scores" :key="score.id" :value="(score as Score)" :song="song"
                    :number="oneBased(index)" />
                <tr v-if="showAdd && !draftValues && authStore.userCan('create', 'score')">
                    <td colspan="2">
                        <button class="button is-primary" @click="addScore">
                            Add
                        </button>
                    </td>
                </tr>
                <ScoreCopy v-if="draftValues" :value="draftValues" @cancel="cancelAdd" @added="onAdded" />
            </tbody>
            <tfoot></tfoot>
        </table>

    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { isNew } from "@/utils";
import { useScores } from "@/application/scoreStore";
import { AxiosError } from "axios";
import { Song } from "@/entities/song";
import { Score } from "@/entities/score";
import { EntityRef } from "@/entities/entity";
import ScoreCopy from "./Score.vue";
import { useAuth } from "@/application/authStore";

type DraftScore = Partial<Score> & {
    song: EntityRef<Song>
}

const props = defineProps<{
    song: Song,
    showAdd: boolean
}>();

const authStore = useAuth();
const loading = ref(true)
const error = ref<string>()
const scoreStore = useScores();
const scores = ref<Array<Score>>([]);
  console.log(props.song);
  
const link = props.song.scores
if (link)
    scoreStore.getAllRelated(link)
        .then(data => scores.value = data)
        .catch(e => error.value = e)
        .finally(() => loading.value = false);
else {
    error.value = "No association found";
}
const draftValues = ref<DraftScore | undefined>(undefined)

const oneBased = (index: number) => index + 1;

const addScore = () => draftValues.value = { song: new EntityRef(props.song) }
const cancelAdd = () => draftValues.value = undefined

const onAdded = (score: Score) => {
    scores.value.push(score)
    draftValues.value = undefined
}

const removeScore = async (score: Score) => {
    if (!isNew(score)) {
        try {
            await scoreStore.delete(score);
        } catch (e) {
            error.value = (e as AxiosError).message
            return;
        }
    }
    scores.value = scores.value.filter(current => current.uri !== score.uri);
}

</script>

<style scoped></style>