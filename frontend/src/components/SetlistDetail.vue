<template>
    <div class="setlist-detail">
        <div v-if="!loading">
            <div class="setlist-info m-3 is-flex is-align-content-flex-start">
                <h1 class="title is-flex-grow-1 mr-3" v-if="!editing">{{ setlist.name }}</h1>
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="editing">
                    <div class="field-label is-normal">
                        <label class="label">Name</label>
                    </div>
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }" >
                            <div class="control">
                                <input v-model="draftValues.name" class="input" type="text" placeholder="Easter Morning Service" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field is-horizontal is-flex-grow-1 mr-3" v-if="editing">
                    <div class="field-label is-normal">
                        <label class="label">Date</label>
                    </div>
                    <div class="field-body">
                        <div class="field" v-bind:class="{ static: !editing }" >
                            <div class="control">
                                <input v-model="draftValues.date" class="input" type="date" />
                            </div>
                        </div>
                    </div>
                </div>
                <div class="field is-grouped">
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
            <div class="subtitle m-3" v-if="!editing">{{ setlist.date }}</div>
        </div>
    </div>
</template>

<script>
import { useSetlists } from "@/stores/setlistStore";
import { inject, onMounted, ref } from "vue";
import {useRoute, useRouter} from "vue-router";

export default {
    setup() {
        const logger = inject('vuejs3-logger');
        const store = useSetlists()
        const route = useRoute()
        const router = useRouter()

        // State
        const setlist = ref({});
        const editing = ref(false);
        const draftValues = ref(null);
        const loading = ref(true);
        const saving = ref(false);
        const error = ref(null);

        // Mounted
        onMounted(() => {
            if (route.name === "NewSetlist") {
                draftValues.value = {};
                editing.value = true;
                loading.value = false;
            } else {
                const setlistId = route.params.id;
                store.get(setlistId).then(value => {
                    console.log("Loading...", loading.value);
                    setlist.value = value;
                    loading.value = false;
                    console.log("value setlist: ", value);
                    console.log("value setlist: ", setlist.value);
                    console.log("Loading..", loading.value);
                });
            }
        })

        // Methods
        const isNew = () => !setlist.value.id;
        const save = () => {
            saving.value = true;
            const newSetlist = draftValues.value;
            if (!newSetlist) {
                return;
            }
            const promise = store.saveToServer(newSetlist);
            promise.then((response) => {
                const isNewSetlist = isNew();
                if(isNewSetlist) {
                    newSetlist.id = response.data.id;
                }
                editing.value = false;
                saving.value = false;
                setlist.value = newSetlist;
                draftValues.value = null;
                if(isNewSetlist) {
                    store.add(setlist.value);
                    router.push({
                        name: "Setlist",
                        params: { id: newSetlist.id },
                    });
                }
            })
        }
        const edit = () => {
            draftValues.value = setlist.value;
            editing.value = true;
        }
        const cancelEdit = () => {
            draftValues.value = null;
            editing.value = false;
        }
        const remove = () => store.remove(setlist.value.id)
            .then(() => {
                router.push({ name: "Repertoire" });
            });

        return { store, setlist, editing, draftValues, loading, saving, save, edit, cancelEdit, remove }
    }
}
</script>
