import { defineStore } from "pinia";
import api from "../services/api";
import { CacheListMap, CacheMap } from "@/types/CacheMaps.js";
import { ApiEntity, ApiEntityWith, Link, WithAssociation } from "@/types/index.js";
import { isNew } from "@/utils";

export const useEntityStore = (name: string, endpoint: string) => defineStore(name, {
    state: () => ({
        objByUri: new CacheMap<string, ApiEntity>(),
        objByRelatedUri: new CacheListMap<string, ApiEntity>(), // e.g. http://localhost:8080/api/songs/1/scores
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
        putRelated(obj: ApiEntity, link?: Link) {
            if (link?.href) this.objByRelatedUri.addTo(link.href, obj)
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
        async fetchRelatedFromAssociation<T extends WithAssociation>(apiEntity: ApiEntityWith<T>, association: string) {
            this.loading = true;
            const key = association as keyof typeof apiEntity._links;
            const uri = apiEntity._links?.[key];
            if (!uri) throw Error(`Link to association ${association} not found.`);

            const response = await api.getAll(uri);
            response.data.forEach(e => this.putRelated(e, uri));
            this.loading = false
            return response.data
        },
        async fetchRelatedFromLink(link: Link) {
            this.loading = true;
            const uri = link.href;
            const response = await api.getAll(uri);
            response.data.forEach(e => this.putRelated(e, link));
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
        async getRelated(link: Link) {
            if (!this.objByRelatedUri.has(link.href)) {
                return await this.fetchRelatedFromLink(link);
            } else {
                this.fetchRelatedFromLink(link); // load in background
                return this.objByRelatedUri.get(link.href);
            }
        },
        async saveToServer<T extends ApiEntity>(obj: T, link?: Link) {
            const response = isNew(obj) ? await api.create(endpoint, obj) : await api.update(obj._links!.self.href, obj);
            this.putRelated(obj, link);
            return response.data;
        },
        async delete(obj: ApiEntity) {
            const uri = obj._links?.self.href!
            await api.delete(obj)
            this.objByUri.delete(uri)
            this.objByRelatedUri.remove(obj)
        }
    }
})
