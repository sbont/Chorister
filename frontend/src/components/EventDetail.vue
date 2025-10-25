<template>
    <div class="event-detail">
        <DetailHeader :mode="pageState" :title="event?.name" :subtitle="event ? format(event?.date) : ''" :onEdit="edit"
            :onDelete="remove" @cancel-edit="cancelEdit"
            :custom-buttons="[{ 'label': 'Export texts', action: exportText }]" />

        <div v-if="!loading">
            <div v-if="error">{{ error }}</div>
            <div class="event-info mx-3" v-if="pageState != 'view' && draftValues">
                <div class="is-flex is-justify-content-flex-end my-3">
                    <div class="field is-horizontal is-flex-grow-1">
                        <div class="field-label is-normal">
                            <label class="label">Name</label>
                        </div>
                        <div class="field-body">
                            <div class="field" v-bind:class="{ static: false }">
                                <div class="control">
                                    <input v-model="draftValues.name" class="input" type="text"
                                        placeholder="Easter Morning Service" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="columns">
                    <div class="column field is-horizontal is-flex-grow-1">
                        <div class="field-label is-normal">
                            <label class="label">Date</label>
                        </div>
                        <div class="field-body">
                            <div class="field" v-bind:class="{ static: false }">
                                <div class="control">
                                    <input v-model="draftValues.date" class="input" type="date" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="column field is-horizontal is-flex-grow-1">
                        <div class="field-label is-normal">
                            <label class="label">Template</label>
                        </div>
                        <div class="field-body">
                            <div class="select">
                                <select v-model="draftTemplate">
                                    <option :value="null">None</option>
                                    <option v-for="oos in allOrdersOfService" :value="oos.uri">
                                        {{ oos.name }}
                                    </option>
                                </select>
                            </div>
                        </div>
                    </div>

                </div>

                <div class="field is-grouped">
                    <p class="control">
                        <button @click="save" class="button is-link" :class="{ 'is-loading': saving }">
                            Save changes
                        </button>
                    </p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { Event } from "@/entities/event";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { isNew } from "@/entities/entity";
import DetailHeader from "./ui/DetailHeader.vue";
import { PageState, Uri } from "@/types";
import { useOrdersOfService } from "@/application/orderOfServiceStore";
import { storeToRefs } from "pinia";

type DraftEvent = Partial<Event>;

const store = useEvents();
const ordersOfServiceStore = useOrdersOfService();
const route = useRoute();
const router = useRouter();

// State
ordersOfServiceStore.ensureInitialized();

const event = ref<Event>();
const draftValues = ref<DraftEvent | null>(null);
const draftTemplate = ref<Uri | null>(null);

const loading = ref(true);
const saving = ref(false);
const error = ref<string | null>(null);
const pageState = ref<PageState>()
const { allOrdersOfService } = storeToRefs(ordersOfServiceStore);

if (route.name === "NewEvent") {
    draftValues.value = {};
    pageState.value = "create"
    loading.value = false;
} else {
    pageState.value = "view";
    const eventId = Number(route.params.id);
    store.get(eventId).then((value) => {
        event.value = value;
        loading.value = false;
    });
}

// Methods
const format = (date: Date) => new Date(date).toLocaleDateString();

const save = async () => {
    saving.value = true;
    let newEvent = draftValues.value as Event;
    if (!newEvent)
        return;

    const isNewEvent = isNew(newEvent);
    if (!newEvent.name && !newEvent.date) {
        error.value = "Either Name or Date is required";
        saving.value = false;
        return;
    } else {
        error.value = null;
    }

    newEvent.template = draftTemplate.value ? { uri: draftTemplate.value } : undefined;
    if (!newEvent.name)
        newEvent.name = new Date(newEvent.date).toLocaleDateString();

    newEvent = await store.save(newEvent);
    pageState.value = "view";
    saving.value = false;
    event.value = newEvent;
    draftValues.value = null;
    if (isNewEvent) {
        await router.push({
            name: "Event",
            params: { id: newEvent.id },
        });
    }
};

const edit = () => {
    draftValues.value = event.value as DraftEvent;
    pageState.value = "edit"
};

const cancelEdit = () => {
    draftValues.value = null;
    pageState.value = "view"
};

const remove = () => {
    if (event.value)
        store.delete(event.value).then(() =>
            router.push({ name: "Planning" }));
}

const exportText = () => {
    const link = router.resolve({
        name: "Export",
        params: { id: event.value?.id },
    });
    window.open(link.href, "_blank");
};

</script>

<style>
.field-label {
    flex-grow: initial;
    min-width: 8rem;
}
</style>

