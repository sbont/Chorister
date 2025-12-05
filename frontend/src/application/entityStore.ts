import { Entity, EntityCollectionRef, EntityRef } from "@/entities/entity";
import { CacheListMap, CacheMap } from "@/types/cache-maps";
import { isNew } from "@/utils";
import { defineStore } from "pinia";
import { computed, inject, Ref, ref } from "vue";
import { Api, ApiEndpoint, ApiKey } from "./api";
import { Uri } from "@/types";

export enum StoreState {
    Uninitialized,
    Initializing,
    Ready
}

export const useEntityStore = <T extends Entity>(name: string, getEndpoint: (api: Api) => ApiEndpoint<T>) => defineStore(name, () => {
    const api = inject(ApiKey);
    if (!api)
        throw new Error("Api not provided");

    const endpoint = getEndpoint(api);
    const objByUri = ref(new CacheMap<Uri, T>()) as Ref<CacheMap<Uri, T>>;
    const objByAssociationUri = ref(new CacheListMap<string, T>());

    // Getters
    const allObjects = computed(() => {
        return [...objByUri.value.values()];
    });
    const allRelated = computed(() => (associationUri: Uri) => {
        return objByAssociationUri.value.get(associationUri);
    });
    const count = computed(() => objByUri.value.size);

    // Actions
    function put(obj: T): void {
        const uri = obj.uri ?? endpoint.getUri(obj.id!);
        objByUri.value.set(uri, obj)
    }

    function addRelated(obj: T, uri: Uri): void {
        objByAssociationUri.value.addTo(uri, obj)
        put(obj)
    }

    function putAllRelated(objects: Array<T>, uri: Uri): void {
        objByAssociationUri.value.delete(uri);
        objByAssociationUri.value.addAllTo(uri, objects);
        objects.forEach(put);
    }

    function refToUri(ref: EntityRef<T>): string {
        if (ref.uri) return ref.uri;
        if (ref.id) return endpoint.getUri(ref.id);
        throw Error("Empty entity reference");
    }

    async function getByRef(ref: EntityRef<T>): Promise<T | undefined> {
        const uri = refToUri(ref);
        return getByUri(uri);
    }


    async function get(id: number): Promise<T | undefined> {
        const uri = endpoint.getUri(id);
        return getByUri(uri);
    }

    async function getByUri(uri: Uri): Promise<T | undefined> {
        if (objByUri.value.has(uri)) {
            fetch(uri); // don't await, just eagerly pass the current data and let the fetch complete in the background
            return objByUri.value.get(uri);
        }

        return await fetch(uri);
    }

    async function fetch(uri: Uri): Promise<T | undefined> {
        const data = await endpoint.getByUri(uri);
        put(data);
        return data;
    }

    async function getAll() {
        if (objByUri.value.size > 0) {
            fetchAll(); // don't await, just eagerly pass the current data and let the fetch complete in the background
            return objByUri.value.values();
        }

        return await fetchAll();
    }

    async function fetchAll() {
        const data = await endpoint.getAll();
        data.forEach(put);
        return data;
    }

    async function getAllRelated(ref: EntityCollectionRef<T>): Promise<T[]> {
        if (!ref.uri)
            throw new Error("URI unknown for related entity");

        const fetch = fetchAllRelated(ref.uri);
        if (objByAssociationUri.value.has(ref.uri)) {
            // don't await the response, just eagerly pass the current data and let the fetch complete in the background
            return objByAssociationUri.value.getOrEmpty(ref.uri);
        }

        return await fetch;
    }

    async function fetchAllRelated(uri: Uri) {
        const data = await endpoint.getAllAssociated(uri);
        putAllRelated(data, uri);
        return data;
    }

    async function save(obj: T) {
        const data = isNew(obj) ? await endpoint.create(obj) : await endpoint.update(obj);
        return data;
    }

    async function remove(obj: T) {
        if (!obj.id && !obj.uri)
            return;

        const uri = obj.uri ?? endpoint.getUri(obj.id!);
        await endpoint.delete(obj);
        objByUri.value.delete(uri);
        objByAssociationUri.value.remove(obj);
    }

    return {
        allObjects,
        allRelated,
        count,
        get,
        getByRef,
        fetch,
        getAll,
        fetchAll,
        addRelated,
        getAllRelated,
        fetchAllRelated,
        save,
        delete: remove
    };
});
