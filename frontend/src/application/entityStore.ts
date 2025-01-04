import { Entity, EntityRef } from "@/entities/entity";
import { CacheListMap, CacheMap } from "@/types/CacheMaps";
import { isNew } from "@/utils";
import { defineStore } from "pinia";
import { computed, Ref, ref } from "vue";
import { ApiEndpoint } from "./api";
import { Uri } from "@/types";

export class EntityStore<DomainEntity extends Entity> {
    protected api: ApiEndpoint<DomainEntity>;
    private name: string;
    private objByUri = ref(new CacheMap<Uri, DomainEntity>()) as Ref<CacheMap<Uri, DomainEntity>>;
    private objByAssociationUri = ref(new CacheListMap<string, DomainEntity>());

    constructor(name: string, apiEndpoint: ApiEndpoint<DomainEntity>) {
        this.name = name;
        this.api = apiEndpoint;
    }

    put(obj: DomainEntity): void {
        const uri = obj.uri ?? this.api.getUri(obj.id!);
        this.objByUri.value.set(uri, obj)
    }

    addRelated(obj: DomainEntity, uri: Uri): void {
        this.objByAssociationUri.value.addTo(uri, obj)
        this.put(obj)
    }

    putAllRelated(objects: Array<DomainEntity>, uri: Uri): void {
        this.objByAssociationUri.value.delete(uri);
        this.objByAssociationUri.value.addAllTo(uri, objects);
        objects.forEach(this.put);
    }

    refToUri(ref: EntityRef<DomainEntity>): string {
        if (ref.uri) return ref.uri;
        if (ref.id) return this.api.getUri(ref.id);
        throw Error("Empty entity reference");
    }

    async getByRef(ref: EntityRef<DomainEntity>): Promise<DomainEntity | undefined> {
        const uri = this.refToUri(ref);
        return this.getByUri(uri);
    }
 

    async get(id: number): Promise<DomainEntity | undefined> {
        const uri = this.api.getUri(id);
        return this.getByUri(uri);
    }

    private async getByUri(uri: Uri): Promise<DomainEntity | undefined> {
        if (this.objByUri.value.has(uri)) {
            this.fetch(uri); // don't await, just eagerly pass the current data and let the fetch complete in the background
            return this.objByUri.value.get(uri);
        } 
        
        return await this.fetch(uri);
    }

    async fetch(uri: Uri): Promise<DomainEntity | undefined> {
        const data = await this.api.getByUri(uri);
        this.put(data);
        return data;
    }

    async getAll() {
        if (this.objByUri.value.size > 0) {
            this.fetchAll(); // don't await, just eagerly pass the current data and let the fetch complete in the background
            return this.objByUri.value.values();
        }

        return await this.fetchAll();
    }

    async fetchAll() {
        const data = await this.api.getAll();
        data.forEach(this.put);
        return data;
    }

    async getRelated(uri: Uri) {
        if (this.objByAssociationUri.value.has(uri)) {
            this.fetchAllRelated(uri); // don't await, just eagerly pass the current data and let the fetch complete in the background
            return this.objByAssociationUri.value.get(uri);
        }
            
        return await this.fetchAllRelated(uri);
    }

    async fetchAllRelated(uri: Uri) {
        const data = await this.api.getAllAssociated(uri);
        this.putAllRelated(data, uri);
        return data;
    }

    async save(obj: DomainEntity ) {
        const data = isNew(obj) ? await this.api.create(obj) : await this.api.update(obj);
        // this.putRelated(obj, link);
        return data;
    }

    async remove(obj: DomainEntity) {
        if (!obj.id && !obj.uri)
            return;

        const uri = obj.uri ?? this.api.getUri(obj.id!);
        await this.api.delete(obj);
        this.objByUri.value.delete(uri);
        this.objByAssociationUri.value.remove(obj);
    }

    use = () => defineStore(this.name, () => {
        // Getters
        const allObjects = computed(() => {
            console.log(`All ${this.name}:`, [...this.objByUri.value.values()]);
            return [...this.objByUri.value.values()];
        });
        const allRelated = computed(() => (associationUri: Uri) => {
            return this.objByAssociationUri.value.get(associationUri);
        });
        const count = computed(() => this.objByUri.value.size);

        return { 
            allObjects, 
            allRelated,
            count, 
            get: this.get,
            getByRef: this.getByRef,
            fetch: this.fetch, 
            getAll: this.getAll, 
            fetchAll: this.fetchAll,
            getAllRelated: this.getRelated,
            fetchAllRelated: this.fetchAllRelated, 
            save: this.save, 
            delete: this.remove 
        };
    });
}

