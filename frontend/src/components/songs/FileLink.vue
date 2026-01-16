<template>
    <a v-if="fileInfoUri" @click.prevent="openFile" @auxclick.prevent="openFile"
        @mousedown.middle.prevent="openFile" href="#" class="icon-text has-text-link">
        <span class="icon">
            <i class="fas fa-cloud-download-alt"></i>
        </span>
        <span>
            {{ description }}
        </span>
    </a>
    <span v-else>
        {{ description }}
    </span>
</template>

<script setup lang="ts">
import { useFiles } from '@/application/fileStore';
import { Score } from '@/entities/score';
import { downloadFile } from '@/services/fileService';
import { extension } from 'mime-types';
import { useToast } from 'primevue/usetoast';
import { PropType, ref } from 'vue';

const props = defineProps({
    fileInfoUri: {
        type: String
    },
    description: { 
        type: String 
    },
    fileName: {
        type: String
    }
});

const fileStore = useFiles();
const toast = useToast();

// state
const openFile = async (event: MouseEvent) => {
    if (!props.fileInfoUri) {
        return;
    }

    try {
        const location = await fileStore.getDownloadUrl(props.fileInfoUri);
        if (event.button == 1)
            window.open(location, "_blank");
        else {
            const response = await downloadFile(location);
            const contentType = response.headers["content-type"]?.toString() ?? "";

            const blob = new Blob([response.data], { type: contentType });
            const link = document.createElement('a')
            link.href = URL.createObjectURL(blob)

            const ext = extension(contentType)
            link.download = `${props.fileName}.${ext}`;
            link.click()
            URL.revokeObjectURL(link.href)
        }
    } catch (e: unknown) {
        toast.add({ severity: "error", summary: "Error while downloading file", detail: (e as Error).message });
    }
}

</script>
