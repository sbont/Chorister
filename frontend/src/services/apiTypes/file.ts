export interface File {
    id: number,
    fileUrl?: string
}

export interface UploadReturnEnvelope {
    fileId: number,
    uploadUrl: string,
}