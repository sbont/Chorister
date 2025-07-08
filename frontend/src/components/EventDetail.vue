<template>
    <div class="event-detail">
        <DetailHeader 
            :mode="pageState" 
            :title="event?.name" 
            :subtitle="event ? format(event?.date) : ''" 
            :onEdit="edit" 
            :onDelete="remove"
            @cancel-edit="cancelEdit"
            :custom-buttons="[{ 'label': 'Export texts', action: exportText }]"
        />

        <div v-if="!loading">
            <div v-if="error">{{ error }}</div>
            <div class="event-info m-3 is-flex is-justify-content-flex-end">
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="pageState != 'view' && draftValues">
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
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="pageState != 'view' && draftValues">
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
                <div class="field is-grouped">
                    <p v-if="pageState != 'view'" class="control">
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
import { PageState } from "@/types";

type DraftEvent = Partial<Event>;

const store = useEvents();
const route = useRoute();
const router = useRouter();

// State
const event = ref<Event>();
const draftValues = ref<DraftEvent | null>(null);
const loading = ref(true);
const saving = ref(false);
const error = ref<string | null>(null);
const pageState = ref<PageState>()
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
            router.push({name: "Planning"}));
}

const exportText = () => {
    const link = router.resolve({
        name: "Export",
        params: { id: event.value?.id },
    });
    window.open(link.href, "_blank");
};

</script>
