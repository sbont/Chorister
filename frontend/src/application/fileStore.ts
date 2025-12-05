import { ApiKey } from "@/application/api";
import { FileInfo } from "@/entities/fileInfo";
import { submitFile } from "@/services/fileService";
import { CacheMap } from "@/types/cache-maps";
import moment from "moment";
import { defineStore } from "pinia";
import { inject, ref } from "vue";

type FileOperation = {
    presignedUrl: string;
    expires?: Date;
    operation: Operation;
}

type Operation = "GET" | "PUT"

export interface UploadReturnEnvelope {
    file: FileInfo,
    uploadUrl: string,
}

export const useFiles = defineStore("files", () => {
    const api = inject(ApiKey)!;

    // state
    const filesById = ref(new CacheMap<number, FileOperation>);

    // actions
    async function getUploadEnvelope(id: number | undefined): Promise<UploadReturnEnvelope> {
        if (id) {
            const file = filesById.value.get(id)
            if (file?.operation == "GET" && file?.expires && file.expires < new Date()) {
                const fileUri = api.files.getUri(id);
                return { file: { id, fileInfoUri: fileUri }, uploadUrl: file.presignedUrl };
            }
        }

        const response = await fetchUploadEnvelope(id)
        const url = new URL(response.uploadUrl);
        const expires = Number.parseInt(url.searchParams.get("X-Amz-Expires") ?? "")
        const creationDate = moment(url.searchParams.get("X-Amz-Date"))

        let expiryDate = undefined;
        if (creationDate && expires)
            expiryDate = creationDate.add(expires, "minutes");
        const file: FileOperation = {
            presignedUrl: url.toString(),
            expires: expiryDate?.toDate(),
            operation: "GET"
        }
        filesById.value.set(response.fileId, file);
        const fileUri = api.files.getUri(response.fileId);
        return { file: { id: response.fileId, fileInfoUri: fileUri }, uploadUrl: file.presignedUrl };
    }

    async function fetchUploadEnvelope(fileId: number | undefined) {
        if (fileId == undefined)
            return api.getUploadReturnEnvelope();
        else
            return api.getUploadReturnEnvelopeForId(fileId);
    }

    async function upload(url: string, file: File) {
        return await submitFile(url, file);
    }

    async function getDownloadUrl(fileUri: string) {
        return await api.getFileLocation(fileUri);
    }

    return {
        getUploadEnvelope, fetchUploadEnvelope, upload, getDownloadUrl
    }
});