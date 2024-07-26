<template>
    <div class="scores m-2 p-3">
        <div class="is-size-4">Scores</div>
        <div class="is-flex is-flex-direction-row is-flex-wrap-wrap">
            <ScoreComponent v-for="score in scores" :key="score._links?.self.href" :value="(score as Score)"
                @remove="removeScore(score)"></ScoreComponent>

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
import ScoreComponent from "./../components/Score.vue"
import { Score, DraftScore } from "./../types";
import { ref } from "vue";
import { isNew } from "./../utils";
import { useScores } from "./../stores/scoreStore";


const props = defineProps({
    songUri: String
})

const loading = ref(true)
const error = ref<string | undefined>(undefined)
const scoreStore = useScores()
const scores = ref<Array<Score>>([]);
scoreStore.fetchRelated(props.songUri as string, "scores").then(data => scores.value = data as Score[]).catch(e => error.value = e).finally(() => loading.value = false)
const draftValues = ref<DraftScore | undefined>(undefined)

const addScore = () => draftValues.value = { song: props.songUri }
const cancelAdd = () => draftValues.value = undefined

const onAdded = (score: Score) => {
    scores.value.push(score)
    draftValues.value = undefined
}

const removeScore = (score: Score) => {
    if (!isNew(score)) {
        scoreStore.delete(score);
    }
    scores.value = scores.value.filter(current => current._links?.self.href !== score._links?.self.href);
}

</script>

<style scoped></style>