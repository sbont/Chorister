import { Score } from "@/entities/score";
import { Uri } from "@/types";
import { CacheMap } from "@/types/CacheMaps";
import { defineStore, storeToRefs } from "pinia";
import { inject, Ref, ref } from "vue";
import { Api, ApiKey } from "./api";
import { useEntityStore } from "./entityStore";

const getEndpoint = (api: Api) => api.scores;
export const useScores = defineStore("chords", () => {
    const core = useEntityStore("scores", getEndpoint)();
    const api = inject(ApiKey);
    if (!api)
        throw new Error("Api not provided");

    // State
    const endpoint = getEndpoint(api);
    const fileIdByUri = ref(new CacheMap<Uri, number>()) as Ref<CacheMap<Uri, number>>;

    // Action
    async function linkFile(score: Score, fileId: number) {
        await endpoint.putFileIdForScore(score, fileId);
        fileIdByUri.value.set(score.uri ?? endpoint.getUri(score.id!), fileId);
    }

    return {
        allRelated: core.allRelated,
        allObject: core.allObjects,
        count: core.count,
        get: core.get,
        getByRef: core.getByRef,
        fetch: core.fetch,
        getall: core.getAll,
        fetchAll: core.fetchAll,
        addRelated: core.addRelated,
        getAllRelated: core.getAllRelated,
        fetchAllRelated: core.fetchAllRelated,
        save: core.save,
        delete: core.delete,
        linkFile };
});



