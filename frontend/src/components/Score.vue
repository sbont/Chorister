<template>
    <div>
        <div v-if="!editing" class="card score mr-2">
            <header class="card-header">
                <p class="card-header-title">
                    <a @click.prevent="openFile" @auxclick.prevent="openFile" @mousedown.middle.prevent="openFile"
                        href="#" class="icon-text">
                        <span class="icon">
                            <i class="fas fa-cloud-download-alt"></i>
                        </span>
                        <span>
                            {{ score.description }}
                        </span>
                    </a>

                </p>
            </header>
            <div class="card-content p-0">
                <div class="content is-flex">
                    {{ error }}
                </div>
            </div>
            <footer class="card-footer">
                <a @click.prevent="edit" href="#" class="card-footer-item">Edit</a>
                <a @click.prevent="$emit('remove')" href="#" class="card-footer-item has-text-danger">Delete</a>
            </footer>
        </div>
        <div v-if="editing" class="card mr-2">
            <div class="card-content-editing">
                <div class="content">
                    <FileUpload name="file[]" :disabled="uploadDisabled()" @uploader="onUpload" @select="selectFile"
                        @remove="removeFile" :multiple="false" :file-limit="1" accept="application/pdf,image/*"
                        :maxFileSize="2000000" invalidFileSizeMessage="File exceeds maximum size of 2GB."
                        choose-label="Browse" customUpload>
                        <template #empty>
                            <span>Drag and drop files to here to upload.</span>
                        </template>
                    </FileUpload>
                    <div class="is-flex is-flex-direction-column card-content pt-0">
                        <div class="field">
                            <label class="label">Description</label>
                            <div class="control">
                                <input class="input" type="text" v-model="draftValues!.description"
                                    placeholder="Version name, instrument, tonality..." />
                            </div>
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
import { useFiles } from '@/application/fileStore';
import { useScores } from "@/application/scoreStore";
import { EntityRef } from '@/entities/entity';
import { Score } from '@/entities/score';
import { Song } from '@/entities/song';
import { downloadFile } from '@/services/fileService';
import api from '@/services/legacyApi';
import { isNew } from "@/utils";
import { AxiosError } from 'axios';
import { extension } from 'mime-types';
import FileUpload, { FileUploadRemoveEvent, FileUploadSelectEvent, FileUploadUploaderEvent } from 'primevue/fileupload';
import { PropType, onMounted, ref } from 'vue';

type DraftScore = Partial<Score> & {
    song: EntityRef<Song>
}

const props = defineProps({
    value: {
        type: Object as PropType<Score | DraftScore>,
        required: true
    },
    song: {
        type: Object as PropType<Song>
    }
})
const emit = defineEmits(["remove", "cancel", "added"])

const scoreStore = useScores();
const fileStore = useFiles();

// state
const score = ref(props.value);
const editing = ref(false);
const draftValues = ref<DraftScore>();
const saving = ref(false);
const error = ref<string>();
const selectedFile = ref<File>()

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
    editing.value = true;
}

const onUpload = async (event: FileUploadUploaderEvent) => {
    const envelope = await fileStore.getUploadEnvelope(score.value?.file?.id!);
    if (!envelope?.uploadUrl)
        return;

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
    return envelope.fileId;
}

const save = async () => {
    error.value = undefined;
    const wasNew = isNew(draftValues.value);
    if (!selectedFile.value) {
        error.value = "No file selected."
        return;
    }

    editing.value = false;
    saving.value = true;
    const fileId = await upload(selectedFile.value);
    if (!fileId) {
        error.value = "No file ID received."
        return;
    }

    const savedScore = await scoreStore.save(score.value as Score)
    savedScore.file = { id: fileId };
    score.value = savedScore;
    await scoreStore.linkFile(savedScore, fileId);

    if (wasNew)
        emit("added", savedScore)

    saving.value = false;
}

const cancelEdit = () => {
    editing.value = false;
    draftValues.value = undefined;
    emit("cancel")
}

const download = async () => {
    const fileId = score.value.file?.id
    if (!fileId)
        return;

    const response = await api.getFile(fileId);
    const blob = new Blob([response.data], { type: 'application/pdf' });
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = "download"
    link.click()
    URL.revokeObjectURL(link.href)
}

const openFile = async (event: MouseEvent) => {
    const fileId = score.value.file?.id
    if (!fileId)
        return;

    try {
        const response = await api.getFile(fileId);
        const location = response.headers["location"];
        if (event.button == 1)
            window.open(location, "_blank");
        else {
            const response = await downloadFile(location);
            const contentType = response.headers['content-type']?.toString() ?? "";
            console.log(response.headers);

            const blob = new Blob([response.data], { type: contentType });
            const link = document.createElement('a')
            link.href = URL.createObjectURL(blob)
            console.log(contentType);

            const ext = extension(contentType)
            link.download = `${props.song?.title} - ${score.value?.description}.${ext}`;
            link.click()
            URL.revokeObjectURL(link.href)
        }
    } catch (e) {
        error.value = JSON.stringify(e);
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

.textarea:not([rows]) {
    max-height: initial;
}

.p-fileupload .p-fileupload-upload-button,
.p-fileupload .p-fileupload-cancel-button {
    display: none;
}

.p-fileupload .p-fileupload-file {
    padding: 0;
}

.p-fileupload-advanced {
    border: none !important;
}

.p-fileupload-advanced .p-fileupload-file-thumbnail,
.p-fileupload-advanced .p-fileupload-file-badge {
    display: none;
}
</style>