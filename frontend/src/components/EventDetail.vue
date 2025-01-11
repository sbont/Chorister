<template>
    <div class="event-detail">
        <div v-if="!loading">
            <div v-if="error">{{ error }}</div>
            <div class="event-info m-3 is-flex is-align-content-flex-start">
                <h1 class="title is-flex-grow-1 mr-3" v-if="!editing">{{ event?.name }}</h1>
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="editing && draftValues">
                    <div class="field-label is-normal">
                        <label class="label">Name</label>
                    </div>
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }">
                            <div class="control">
                                <input
                                    v-model="draftValues.name"
                                    class="input"
                                    type="text"
                                    placeholder="Easter Morning Service"
                                />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="editing && draftValues">
                    <div class="field-label is-normal">
                        <label class="label">Date</label>
                    </div>
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }">
                            <div class="control">
                                <input v-model="draftValues.date" class="input" type="date" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field is-grouped">
                    <p v-if="!editing" class="control">
                        <button @click="exportText" class="button is-link">Export texts</button>
                    </p>
                    <p v-if="!editing" class="control">
                        <button @click="edit" class="button is-link">Edit</button>
                    </p>
                    <p v-if="!editing" class="control">
                        <button @click="remove" class="button is-danger">Delete</button>
                    </p>
                    <p v-if="editing" class="control">
                        <button @click="save" class="button is-link" :class="{ 'is-loading': saving }">
                            Save changes
                        </button>
                    </p>
                    <p v-if="editing" class="control">
                        <button @click="cancelEdit" class="button">Cancel</button>
                    </p>
                </div>
            </div>
            <div class="subtitle m-3" v-if="!editing && event">{{ format(event.date) }}</div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { Event } from "@/entities/event";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { isNew } from "@/entities/entity";

type DraftEvent = Partial<Event>;

const store = useEvents();
const route = useRoute();
const router = useRouter();

// State
const event = ref<Event>();
const editing = ref(false);
const draftValues = ref<DraftEvent | null>(null);
const loading = ref(true);
const saving = ref(false);
const error = ref<string | null>(null);
if (route.name === "NewEvent") {
    draftValues.value = {};
    editing.value = true;
    loading.value = false;
} else {
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
    editing.value = false;
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
    editing.value = true;
};

const cancelEdit = () => {
    draftValues.value = null;
    editing.value = false;
};

const remove = () => {
    if (event.value)
        store.delete(event.value).then(() =>
            router.push({name: "Repertoire"}));
}

const exportText = () => {
    const link = router.resolve({
        name: "Export",
        params: { id: event.value?.id },
    });
    window.open(link.href, "_blank");
};

</script>
