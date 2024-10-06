<template>
    <div class="chords">
        <div v-if="!editing" class="card mr-2">
            <header class="card-header">
                <p class="card-header-title">
                    <span>{{ chords.key ? keyLabelMapping[chords.key] : "" }}</span>
                    <span class="ml-2 has-text-weight-normal">{{ chords.description }}</span>
                </p>
            </header>
            <div class="card-content">
                <div class="content mono" v-html="chords.chords"></div>
            </div>
            <footer class="card-footer">
                <a @click.prevent="edit" href="#" class="card-footer-item">Edit</a>
                <a @click.prevent="$emit('remove')" href="#" class="card-footer-item has-text-danger">Delete</a>
            </footer>
        </div>
        <div v-if="editing" class="card mr-2">
            <div class="card-content card-content-editing">
                <div class="content is-flex is-flex-direction-column">
                    <div class="field field-flex columns">
                        <div class="field-flex-col column is-one-third">
                            <div class="field">
                                <label class="label">Key</label>
                                <div class="control">
                                    <div class="select">
                                        <select v-model="draftValues.key">
                                            <option>Select key...</option>
                                            <option v-for="(key, index) in keyOptions" class="song" :key="key"
                                                    draggable="true" v-bind:value="key">{{ keyLabelMapping[key] }}
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="field-flex-col column">
                            <div class="field">
                                <label class="label">Description</label>
                                <div class="control">
                                    <input class="input" type="text" v-model="draftValues.description"
                                           placeholder="Version name, instrument, ..."/>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="field">
                        <label class="label">Chords</label>
                        <div class="control">
                            <editor-content :editor="editor"/>
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
import { PropType, ref } from 'vue'
import { Key, KeyLabelMapping } from "@/types/Key";
import { useChords } from "@/stores/chordsStore";
import { ApiEntity, Chords, DraftChords } from "@/types";
import { isNew } from "@/utils";
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import TextStyle from '@tiptap/extension-text-style'
import FontFamily from '@tiptap/extension-font-family'

const props = defineProps({
    value: {
        type: Object as PropType<Chords | DraftChords>,
        required: true
    }
})
const emit = defineEmits(["remove", "cancel", "added"])

const chordsStore = useChords();
const keyLabelMapping = KeyLabelMapping
const editor = useEditor({
    content: null,
    extensions: [
        StarterKit,
        TextStyle,
        FontFamily
    ],
    editorProps: {
        attributes: {
            class: 'textarea textarea-resizable focus:outline-none content mono',
        },
    },
    parseOptions: {
        preserveWhitespace: true
    },
    editable: true,
})

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
    if (editor.value) {
        editor.value.commands.setContent(draftValues.value.chords)
        editor.value.chain().focus().setFontFamily('monospace')
    }
}

const save = () => {
    chords.value = draftValues.value;
    editing.value = false;
    saving.value = true;
    const html = editor.value?.getHTML();
    chords.value.chords = html;
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
    width: 30rem;
}

.chords .card .card-header .card-header-title,
.chords .card .card-content {
    padding: 0.75rem 1rem !important;
}

.chords .content.mono {
    font-family: 'Fira Mono', Courier, monospace;
    font-size: 10pt;
    white-space: pre-wrap;
}

.chords .content.mono p {
    margin-bottom: 0;
    min-height: 20px;
}
</style>