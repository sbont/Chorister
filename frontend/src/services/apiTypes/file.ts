import { File as DomainFile } from "@/entities/file";
import { fromDomain, toDomain } from ".";

export interface File {
    id?: number,
    fileUrl?: string
}

export interface UploadReturnEnvelope {
    fileId: number,
    uploadUrl: string,
}

export function fromDomainFile(file: DomainFile): File {
    return { ... fromDomain(file) };
}