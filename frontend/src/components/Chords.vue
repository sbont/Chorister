<template>
    <div>
        <div v-if="!editing" class="card chords mr-2">
            <header class="card-header">
                <p class="card-header-title">
                    {{ chords.key ? keyLabelMapping[chords.key] : "(No key)" }}
                </p>
            </header>
            <div class="card-content p-0">
                <div class="content is-flex">
                    {{  chords.chords }}
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
                            <input class="input" type="text" v-model="draftValues.description"
                                placeholder="Version name, instrument, ..." />
                        </div>
                    </div>
                    <div class="field">
                        <label class="label">Key</label>
                        <div class="control">
                            <div class="select">
                                <select v-model="draftValues.key">
                                    <option>Select key...</option>
                                    <option v-for="(key, index) in keyOptions" class="song" :key="key" draggable="true" v-bind:value="key">{{ keyLabelMapping[key] }}</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label class="label">Chords</label>
                        <div class="control">
                            <input class="input" type="textarea" v-model="draftValues.chords" />
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
<script setup lang="ts">
import { PropType, onMounted, ref } from 'vue'
import { Key, KeyLabelMapping } from "@/types/Key";
import { useChords } from "@/stores/chordsStore";
import { useRoute, useRouter } from "vue-router";
import {ApiEntity, Chords, DraftChords} from "@/types";
import { isNew } from "@/utils";

const props = defineProps({
    value: {
        type: Object as PropType<Chords | DraftChords>,
        required: true
    }
})
const emit = defineEmits(["remove", "cancel", "added"])

const chordsStore = useChords();
const route = useRoute();
const router = useRouter()
const keyLabelMapping = KeyLabelMapping

// state
const chords = ref(props.value);
const editing = ref(false);
const draftValues = ref();
const saving = ref(false);
const error = ref(null);
const keyOptions = Object.values(Key);

if (isNew(chords.value)) {
  draftValues.value = chords.value;
  editing.value = true;
} 

// Methods
const edit = () => {
    draftValues.value = chords.value;
    editing.value = true;
}

const save = () => {
    chords.value = draftValues.value;
    editing.value = false;
    saving.value = true
    chordsStore.saveToServer(<ApiEntity>chords.value)
        .then(newChords => {
            emit("added", newChords)
        })
        .finally(() => saving.value = false)
}

const cancelEdit = () => {
    editing.value = false;
    draftValues.value = null;
    emit("cancel")
}
</script>

<style>
[v-cloak] {
    display: none;
}

.chords {
    max-width: 20rem;
}

.card-content-editing {
    height: 249px;
}
</style>