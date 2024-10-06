import { defineStore } from "pinia";
import api from "../api.js";
import { CacheListMap, CacheMap } from "@/types/CacheMaps.js";
import { ApiEntity } from "@/types/index.js";

export const useEntityStore = (name: string, endpoint: string) => defineStore(name, {
    state: () => ({
        objByUri: new CacheMap<string, ApiEntity>(),
        objByRelatedUri: new CacheListMap<string, ApiEntity>(),
        loading: false,
        error: null as string | null
    }),
    getters: {
        getAll: (state) => {
            console.log(`All ${name}:`, [...state.objByUri.values()]);
            return [...state.objByUri.values()];
        },
        count: (state) => state.objByUri.size
    },
    actions: {
        put(obj: ApiEntity) {
            this.objByUri.set(obj._links?.self.href!, obj)
        },
        putRelated(obj: ApiEntity, parentUri?: string) {
            if (parentUri) this.objByRelatedUri.addTo(parentUri, obj)
            this.put(obj)
        },
        async fetch(uri: string) {
            this.loading = true
            const response = await api.getOne<ApiEntity>(uri)
            this.put(response.data)
            this.loading = false
            return response.data
        },
        async fetchAll() {
            this.loading = true;
            const response = await api.getAll(endpoint);
            response.data.forEach(this.put);
            this.loading = false
            return response.data
        },
        async fetchRelated(uri: string, association: string) {
            this.loading = true;
            const response = await api.getAllRelated(uri, association);
            console.log(response)
            console.log(response.data)
            response.data.forEach(e => this.putRelated(e, uri));
            this.loading = false
            return response.data
        },
        async get(uri: string) {
            if (!this.objByUri.has(uri)) {
                return await this.fetch(uri);
            } else {
                return this.objByUri.get(uri);
            }
        },
        async getRelated(uri: string, association: string) {
            if (!this.objByRelatedUri.has(uri)) {
                return await this.fetchRelated(uri, association);
            } else {
                this.fetchRelated(uri, association); // load in background
                return this.objByRelatedUri.get(uri);
            }
        },
        async saveToServer(obj: ApiEntity, relatedUri?: string) {
            if (obj._links?.self.href) {
                return api.update(obj._links.self.href, obj).then(newObj => this.putRelated(obj, relatedUri));
            } else {
                return api.create(endpoint, obj).then(newObj => this.putRelated(obj, relatedUri));
            }
        },
        async delete(obj: ApiEntity) {
            const uri = obj._links?.self.href!
            await api.delete(obj)
            this.objByUri.delete(uri)
            this.objByRelatedUri.remove(obj)
        }
    }
})
