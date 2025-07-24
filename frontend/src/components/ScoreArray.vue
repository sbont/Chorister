<template>
    <div class="scores m-2 p-3">
        <div class="is-size-4">Scores</div>
        <div v-if="error" class="is-danger">{{ error }}</div>
        <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
            <ScoreComponent v-for="score in scores" :key="score.id" :value="(score as Score)"
                :song="song" @remove="removeScore(score)"></ScoreComponent>

            <div v-if="!draftValues">
                <button class="button is-primary" @click="addScore">
                    Add
                </button>
            </div>
            <div v-else>
                <ScoreComponent :value="draftValues" @cancel="cancelAdd" @added="onAdded"></ScoreComponent>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import ScoreComponent from "@/components/Score.vue"
import { ref } from "vue";
import { isNew } from "@/utils";
import { useScores } from "@/application/scoreStore";
import { AxiosError } from "axios";
import { Song } from "@/entities/song";
import { Score } from "@/entities/score";
import { EntityRef } from "@/entities/entity";

type DraftScore = Partial<Score> & {
    song: EntityRef<Song>
}

const props = defineProps<{
    song: Song
}>();

const loading = ref(true)
const error = ref<string>()
const scoreStore = useScores();
const scores = ref<Array<Score>>([]);
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