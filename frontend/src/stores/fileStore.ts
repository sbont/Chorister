import api from "@/services/api";
import { CacheMap, CacheListMap } from "@/types/CacheMaps";
import { defineStore } from "pinia";
import moment from "moment";
import { submitFile } from "@/services/fileService";

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

export const useFiles = defineStore('files', {
    state: () => ({
        filesById: new CacheMap<number, FileOperation>
    }),
    getters: {},
    actions: {
        async getUploadEnvelope(id: number | undefined): Promise<UploadReturnEnvelope> {
            if (id) {
                const file = this.filesById.get(id)
                if (file?.operation == "GET" && file?.expires && file.expires < new Date())
                    return { fileId: id, uploadUrl: file.presignedUrl };
            }

            const response = await this.fetchUploadEnvelope(id)
            const url = new URL(response.data.uploadUrl)
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
            this.filesById.set(response.data.fileId, file);
            return { fileId: response.data.fileId, uploadUrl: file.presignedUrl };
        },

        async fetchUploadEnvelope(id: number | undefined) {
            if (id == undefined)
                return api.getUploadReturnEnvelope();
            else
                return api.getUploadReturnEnvelopeForId(id);
        },

        async upload(url: string, file: File) {
            return await submitFile(url, file);
        }

    }
})