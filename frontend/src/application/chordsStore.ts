import { useEntityStore } from "@/application/entityStore";
import { Api } from "./api";

const getEndpoint = (api: Api) => api.chords;
export const useChords = useEntityStore("chords", getEndpoint);
