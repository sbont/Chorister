import { OrderOfService } from "@/entities/orderOfService";
import { CacheMap } from "@/types/CacheMaps";
import { defineStore } from "pinia";
import { computed, inject, ref } from "vue";
import { ApiKey } from "./api";
import { useChoir } from "./choirStore";

export enum Status {
    Empty,
    Initializing,
    Ready
}

export const useOrdersOfService = defineStore("ordersOfService", () => {
    const api = inject(ApiKey)!;
    const choirStore = useChoir();

    // state
    const status = ref<Status>(Status.Empty);
    const ordersOfService = ref(new CacheMap<number, OrderOfService>());

    // computed
    const allOrdersOfService = computed(() => [...ordersOfService.value.values()]);

    // actions
    async function ensureInitialized() {
        if (status.value == Status.Empty) {
            await initialize();
        }
    }

    async function initialize() {
        status.value = Status.Initializing;
        const choir = await choirStore.getChoir();
        const rite = choir.rite!;
        const result = await api.ordersOfService.getAllForRite(rite.uri);
        result.forEach(oos => ordersOfService.value.set(oos.id, oos));
        status.value = Status.Ready;
    }

    return { status, ordersOfService, allOrdersOfService, ensureInitialized };
});