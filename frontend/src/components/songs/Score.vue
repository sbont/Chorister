<template>
    <tr v-if="state == State.Ready">
        <td>
            {{ number }}
        </td>
        <td>
            <FileLink :file-info-uri="score.file?.fileInfoUri" :description="score.description" :file-name="song?.title + ' - ' + score.description" />
        </td>
        <td>
            {{ score.key ? keyLabelMapping[score.key] : "" }}
        </td>
        <td class="shrink" v-if="authStore.userCan('update', 'score')">
            <button class="button is-info is-inverted is-small" @click.prevent="edit">
                <span class="icon is-small">
                    <i class="fas fa-pen"></i>
                </span>
            </button>
        </td>
        <td class="shrink" v-if="authStore.userCan('delete', 'score')">
            <button class="button is-danger is-inverted is-small" @click="$emit('remove')">
                <span class="icon is-small">
                    <i class="fas fa-times"></i>
                </span>
            </button>
        </td>
    </tr>
    <tr v-else>
        <td colspan="2">
            <div class="is-flex">
                <FileUpload name="file[]" :disabled="uploadDisabled()" @uploader="onUpload" @select="selectFile"
                    @remove="removeFile" :multiple="false" :file-limit="1" accept="application/pdf,image/*"
                    :maxFileSize="2000000" invalidFileSizeMessage="File exceeds maximum size of 2GB."
                    choose-label="Browse" customUpload>
                </FileUpload>
                <div class="field is-flex-grow-1 ml-3">
                    <div class="control">
                        <input class="input" type="text" v-model="draftValues!.description"
                            placeholder="Version name, instrument, tonality..." />
                    </div>
                </div>
                <div v-if="error" class="card-content p-0">
                    <div class="content is-flex has-text-danger">
                        {{ error }}
                    </div>
                </div>
            </div>
        </td>
        <td>
            <div class="field is-flex-grow-1 ml-3">
                <div class="control">
                    <div class="select">
                        <select v-model="draftValues!.key">
                            <option v-bind:value="undefined" disabled>Select...</option>
                            <option v-for="(key, i) in keyOptions" class="song" :key="key" draggable="true"
                                v-bind:value="key">{{ keyLabelMapping[key] }}
                            </option>
                        </select>
                    </div>
                </div>
            </div>
        </td>
        <td class="shrink">
            <button class="button is-info is-small" @click.prevent="save"
                :class="{ 'is-loading': state == State.Saving, 'is-inverted': state == State.Editing }">
                <span class="icon is-small">
                    <i class="fas fa-check"></i>
                </span>
            </button>
        </td>
        <td class="shrink">
            <button class="button is-danger is-inverted is-small" @click.prevent="cancelEdit"
                :disabled="state == State.Saving">
                <span class="icon is-small">
                    <i class="fas fa-times"></i>
                </span>
            </button>
        </td>
    </tr>
</template>

<script setup lang="ts">
import { useAuth } from '@/application/authStore';
import { useFiles } from '@/application/fileStore';
import { useScores } from "@/application/scoreStore";
import { EntityRef } from '@/entities/entity';
import { Score } from '@/entities/score';
import { Song } from '@/entities/song';
import { downloadFile } from '@/services/fileService';
import { Key, KeyLabelMapping } from '@/types/key';
import { isNew } from "@/utils";
import { AxiosError } from 'axios';
import { extension } from 'mime-types';
import FileUpload, { FileUploadRemoveEvent, FileUploadSelectEvent, FileUploadUploaderEvent } from 'primevue/fileupload';
import { PropType, onMounted, ref } from 'vue';
import FileLink from './FileLink.vue';

type DraftScore = Partial<Score> & {
    song: EntityRef<Song>
}

enum State {
    Ready,
    Editing,
    Saving
}

const props = defineProps({
    value: {
        type: Object as PropType<Score | DraftScore>,
        required: true
    },
    number: { type: Number },
    song: {
        type: Object as PropType<Song>
    }
})
const emit = defineEmits(["remove", "cancel", "added"])

const scoreStore = useScores();
const fileStore = useFiles();
const authStore = useAuth();

// state
const score = ref(props.value);
const state = ref<State>(State.Ready);
const draftValues = ref<DraftScore>();
const error = ref<string>();
const selectedFile = ref<File>()

const keyLabelMapping = KeyLabelMapping
const keyOptions = Object.values(Key);

// Computed
onMounted(() => {
    if (isNew(score.value)) {
        edit();
    }
});

const uploadDisabled = () => !!selectedFile.value;

// Methods
const selectFile = (event: FileUploadSelectEvent) => {
    selectedFile.value = (event.files as File[])[0];
    if (draftValues.value && !draftValues.value.description) {
        const name = selectedFile.value.name;
        draftValues.value.description = name.substring(0, name.indexOf("."));
    }
}

const removeFile = (_: FileUploadRemoveEvent) => {
    selectedFile.value = undefined;
}

const edit = async () => {
    draftValues.value = score.value as DraftScore;
    state.value = State.Editing;
}

const onUpload = async (event: FileUploadUploaderEvent) => {
    const files = event.files as File[];
    if (files.length !== 1) {
        error.value = "Number of selected files is not equal to 1."
        return;
    }
    await upload(files[0]);
}

const upload = async (file: File) => {
    const envelope = await fileStore.getUploadEnvelope(score.value?.file?.id);
    if (!envelope?.uploadUrl)
        return;

    try {
        await fileStore.upload(envelope.uploadUrl, file)
    } catch (e) {
        console.log(e);
        error.value = (e as AxiosError).message;
    }
    return envelope.file;
}

const save = async () => {
    error.value = undefined;
    const wasNew = isNew(draftValues.value);
    if (!selectedFile.value && wasNew) {
        error.value = "No file selected."
        return;
    }

    state.value = State.Saving;

    let fileInfo;
    if (selectedFile.value) {
        fileInfo = await upload(selectedFile.value);
        if (!fileInfo) {
            error.value = "No file ID received."
            return;
        }
    }

    const savedScore = await scoreStore.save(draftValues.value as Score)
    if (fileInfo && wasNew) {
        savedScore.file = fileInfo;
        await scoreStore.linkFile(savedScore, fileInfo.id!!);
    }
    score.value = savedScore;

    if (wasNew)
        emit("added", savedScore)

    state.value = State.Ready;
}

const cancelEdit = () => {
    state.value = State.Ready;
    draftValues.value = undefined;
    emit("cancel")
}

</script>

<style>
[v-cloak] {
    display: none;
}

.score {
    max-width: 20rem;
}

.textarea:not([rows]) {
    max-height: initial;
}

.table td {
    align-content: center;
}

.table .shrink {
    width: 0;
}

.p-fileupload .p-fileupload-upload-button,
.p-fileupload .p-fileupload-cancel-button {
    display: none;
}

.p-fileupload .p-fileupload-file,
.p-fileupload .p-fileupload-header {
    padding: 0;
}

.p-fileupload .p-fileupload-content {
    display: none;
}

.p-fileupload-advanced {
    border: none !important;
}

.p-fileupload-advanced .p-fileupload-file-thumbnail,
.p-fileupload-advanced .p-fileupload-file-badge {
    display: none;
}
</style>