<template>
    <div class="setlist-detail">
        <div v-if="!loading">
            <div v-if="error">{{ error }}</div>
            <div class="setlist-info m-3 is-flex is-align-content-flex-start">                
                <h1 class="title is-flex-grow-1 mr-3" v-if="!editing">{{ setlist?.name }}</h1>
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="editing && draftValues">
                    <div class="field-label is-normal">
                        <label class="label">Name</label>
                    </div>
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }">
                            <div class="control">
                                <input v-model="draftValues.name" class="input" type="text"
                                       placeholder="Easter Morning Service"/>
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
                                <input v-model="draftValues.date" class="input" type="date"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field is-grouped">
                    <p v-if="!editing" class="control">
                        <button
                            @click="exportText"
                            class="button is-link"
                        >
                            Export texts
                        </button>
                    </p>
                    <p v-if="!editing" class="control">
                        <button
                            @click="edit"
                            class="button is-link"
                        >
                            Edit
                        </button>
                    </p>
                    <p v-if="!editing" class="control">
                        <button
                            @click="remove"
                            class="button is-danger"
                        >
                            Delete
                        </button>
                    </p>
                    <p v-if="editing" class="control">
                        <button
                            @click="save"
                            class="button is-link"
                            :class="{ 'is-loading': saving }"
                        >
                            Save changes
                        </button>
                    </p>
                    <p v-if="editing" class="control">
                        <button @click="cancelEdit" class="button">
                            Cancel
                        </button>
                    </p>
                </div>
            </div>
            <div class="subtitle m-3" v-if="!editing && setlist">{{ format(setlist.date) }}</div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useSetlists } from "@/stores/setlistStore";
import { Setlist } from "@/types";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

type DraftSetlist = Partial<Setlist>

const store = useSetlists()
const route = useRoute()
const router = useRouter()

// State
const setlist = ref<Setlist>();
const editing = ref(false);
const draftValues = ref<DraftSetlist | null>(null);
const loading = ref(true);
const saving = ref(false);
const error = ref<string | null>(null);

// Mounted
onMounted(() => {
    if (route.name === "NewSetlist") {
        draftValues.value = {};
        editing.value = true;
        loading.value = false;
    } else {
        const setlistId = Number(route.params.id);
        store.get(setlistId).then(value => {
            setlist.value = value;
            loading.value = false;
        });
    }
})

// Methods
const format = (date: Date) => new Date(date).toLocaleDateString();
const isNew = () => !setlist.value?.id;
const save = () => {
    saving.value = true;
    const newSetlist = draftValues.value as Setlist;
    if (!newSetlist) return;
    
    if (!newSetlist.name && !newSetlist.date) {
        error.value = "Either Name or Date is required";
        saving.value = false;
        return;
    } else {
        error.value = null;
    }
    
    if (!newSetlist.name)
        newSetlist.name = new Date(newSetlist.date).toLocaleDateString()

    const promise = store.saveToServer(newSetlist);
    promise.then((response) => {
        const isNewSetlist = isNew()
        if (isNewSetlist) {
            newSetlist.id = response.data.id;
        }
        editing.value = false;
        saving.value = false;
        setlist.value = newSetlist;
        draftValues.value = null;
        if (isNewSetlist) {
            store.put(setlist.value);
            router.push({
                name: "Setlist",
                params: {id: newSetlist.id},
            });
        }
    })
}

const edit = () => {
    draftValues.value = setlist.value as DraftSetlist;
    editing.value = true;
}

const cancelEdit = () => {
    draftValues.value = null;
    editing.value = false;
}

const remove = () => store.remove(setlist.value?.id as number)
    .then(() => {
        router.push({name: "Repertoire"});
    });

const exportText = () => {
    const link = router.resolve({
        name: "Export",
        params: {id: setlist.value?.id},
    });
    window.open(link.href, '_blank');
}

</script>
