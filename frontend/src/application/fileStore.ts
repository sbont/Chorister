import { CacheMap, CacheListMap } from "@/types/CacheMaps";
import { defineStore } from "pinia";
import moment from "moment";
import { submitFile } from "@/services/fileService";
import { inject, ref } from "vue";
import { ApiKey } from "@/application/api";

type FileOperation = {
    presignedUrl: string;
    expires?: Date;
    operation: Operation;
}

type Operation = "GET" | "PUT"

export interface UploadReturnEnvelope {
    fileId: number,
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
            if (file?.operation == "GET" && file?.expires && file.expires < new Date())
                return { fileId: id, uploadUrl: file.presignedUrl };
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
        return { fileId: response.fileId, uploadUrl: file.presignedUrl };
    }

    async function fetchUploadEnvelope(id: number | undefined) {
        if (id == undefined)
            return api.getUploadReturnEnvelope();
        else
            return api.getUploadReturnEnvelopeForId(id);
    }

    async function upload(url: string, file: File) {
        return await submitFile(url, file);
    }
    
    return {
        getUploadEnvelope, fetchUploadEnvelope, upload,
    }
});