import { Score } from "@/types";
import api from "../api";

export async function getUploadUrlForScore(score: Score) {
    const selfUri = score._links?.self.href;
    if (!selfUri)
        return undefined;

    var response = await api.getUploadUrlForScore(selfUri);
    return new URL(response.data);
}

export async function getUploadUrl() {
    var envelope = api.getUploadReturnEnvelope();
    
}