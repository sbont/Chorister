import axios from "axios";

export async function submitFile(objectUri: string, file: File) {
    const buffer = await file.arrayBuffer();
    return axios.put(objectUri, buffer, {
        headers: {
            'Content-Type': file.type
        }
    });
}