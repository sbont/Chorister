<template>
    <div>
        <div v-if="!editing" class="card score mr-2">
            <header class="card-header">
                <p class="card-header-title">
                    {{ score.description }}
                </p>
            </header>
            <div class="card-content p-0">
                <div class="content is-flex">
                    <iframe
                        :src="previewUrl"
                        width="200"
                        height="200"
                    ></iframe>
                </div>
            </div>
            <footer class="card-footer">
                <a @click.prevent="edit" href="#" class="card-footer-item">Edit</a>
                <a @click.prevent="$emit('remove')" href="#" class="card-footer-item has-text-danger">Delete</a>
            </footer>
        </div>
        <div v-if="editing" class="card mr-2">
            <div class="card-content card-content-editing">
                <div class="content is-flex is-flex-direction-column">
                    <div class="field">
                        <label class="label">Description</label>
                        <div class="control">
                            <input
                                class="input"
                                type="text"
                                v-model="draftValues.description"
                                placeholder="Version name, instrument, tonality..."
                            />
                        </div>
                    </div>
                    <div class="field">
                        <label class="label">Google Drive URL</label>
                        <div class="control">
                            <input
                                class="input"
                                type="text"
                                v-model="draftValues.fileUrl"
                                placeholder="https://drive.google.com/file/d/..."
                            />
                        </div>
                    </div>
                </div>
            </div>
            <footer class="card-footer">
                <a href="#" class="card-footer-item" @click.prevent="save">
                    Save
                </a>
                <a href="#" class="card-footer-item has-text-danger" @click.prevent="cancelEdit">
                    Cancel
                </a>
            </footer>
        </div>
    </div>
</template>
<script>
import api from "../api.js";
import {computed, onMounted, ref } from 'vue'
import { useScores } from "@/stores/scoreStore";
import { useRoute, useRouter } from "vue-router";

export default {
    props: {
        value: Object
    },
    emits: ["remove"],
    setup(props, { emit }) {
        const scoreStore = useScores();
        const route = useRoute();
        const router = useRouter()

        // state
        const score = ref(props.value);
        const editing = ref(false);
        const draftValues = ref();
        const saving = ref(false);
        const error = ref(null);

        // Computed
        const isNew = computed(() => !score.value.id);
        const previewUrl = computed(() => score.value.fileUrl?.replace("/view", "/preview"));

        onMounted(() => {
            if (!score.value.id) {
                draftValues.value = score.value;
                editing.value = true;
            }
        });

        // Methods
        const edit = () => {
            draftValues.value = score.value;
            editing.value = true;
        }

        const save = () => {
            score.value = draftValues.value;
            editing.value = false;
            console.log(score.value);
            if(score.value.id) {
                api.updateScoreForId(score.value.id, score.value);
            } else {
                api.createNewScore(score.value);
            }
        }

        const cancelEdit = () => {
            editing.value = false;
            draftValues.value = null;
            if(!score.value.id) {
                emit("remove");
            }
        }
        return { score, editing, draftValues, saving, previewUrl, edit, save, cancelEdit }
    }
}
</script>

<style>
[v-cloak] {
    display: none;
}
.score {
    max-width: 20rem;
}
.card-content-editing {
    height: 249px;
}
</style>