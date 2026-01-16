<template>
    <div class="scores m-2 p-3">
        <div class="is-size-4">Scores</div>

        <table v-cloak class="table is-hoverable is-fullwidth scores-table">
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
                <ScoreComponent
                    v-for="(score, index) in scores" :key="score.id" :value="(score as Score)" :song="song" :number="oneBased(index)"
                    @remove="removeScore(score)" />
                <tr v-if="showAdd && !draftValues && authStore.userCan('create', 'score')">
                    <td colspan="2">
                        <button class="button is-primary" @click="addScore">
                            Add
                        </button>
                    </td>
                </tr>
                <ScoreComponent v-if="draftValues" :value="draftValues" @cancel="cancelAdd" @added="onAdded" />
            </tbody>
            <tfoot></tfoot>
        </table>

    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useScores } from "@/application/scoreStore";
import { AxiosError } from "axios";
import { Song } from "@/entities/song";
import { Score } from "@/entities/score";
import { EntityRef, isNew } from "@/entities/entity";
import ScoreComponent from "./Score.vue";
import { useAuth } from "@/application/authStore";
import { useToast } from "primevue/usetoast";

type DraftScore = Partial<Score> & {
    song: EntityRef<Song>
}

const props = defineProps<{
    song: Song,
    showAdd: boolean
}>();

const authStore = useAuth();
const loading = ref(true)
const scoreStore = useScores();
const scores = ref<Array<Score>>([]);
  console.log(props.song);
const toast = useToast(); 
  
const link = props.song.scores
if (link)
    scoreStore.getAllRelated(link)
        .then(data => scores.value = data)
        .catch(e => toast.add({ summary: "Error while retrieving scores", detail: (e as AxiosError).message, severity: "error" }))
        .finally(() => loading.value = false);
else {
    toast.add({ summary: "Error while retrieving score", detail: "Associated scores not found", severity: "error" });
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
            toast.add({ summary: "Error while deleting score", detail: (e as AxiosError).message, severity: "error" });
        }
    }
    scores.value = scores.value.filter(current => current.uri !== score.uri);
}

</script>

<style scoped></style>