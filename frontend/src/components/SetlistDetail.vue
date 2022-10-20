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
import api from "../api";
import { useSetlists } from "@/stores/setlist";


const SetlistDetail = {
    name: "SetlistDetail",

    data: function () {
        return {
            setlist: {},
            editing: false,
            draftValues: null,
            loading: true,
            saving: false,
            error: null,
        };
    },

    computed: {
        isNew: function () {
            return !this.setlist.id;
        },
    },

    mounted: function () {
        if (this.$route.name === "NewSetlist") {
            this.draftValues = {};
            this.editing = true;
            this.loading = false;
        } else {
            var setlistId = this.$route.params.id;
            let setlistLoaded = api.getSetlistById(setlistId)
                .then((response) => {
                    this.$log.debug("Setlist loaded: ", response.data);
                    this.setlist = response.data;
                    this.loading = false;
                })
                .catch((error) => {
                    this.$log.debug(error);
                    this.error = "Failed to load setlist";
                    this.loading = false;
                });
        }
    },

    methods: {
        save: function() {
            this.saving = true;
            var setlist = this.draftValues;
            if (!setlist) {
                return;
            }
            const promise = this.saveToServer(setlist);
            promise.then((response) => {
                if(this.isNew) {
                    setlist.id = response.data.id;
                }
                this.editing = false;
                this.saving = false;
                this.setlist = setlist;
                this.draftValues = null;
                if(this.isNew) {
                    this.$router.push({
                        name: "Setlist",
                        params: { id: setlist.id },
                    });
                    let store = useSetlists();
                    store.add(setlist);
                } 
            })
        },

        saveToServer: function(setlist) {
            if (setlist.id) {
                return api.updateSetlistForId(setlist.id, setlist);
            } else {
                return api.createNewSetlist(setlist);
            }
        },

        edit: function() {
            this.draftValues = this.setlist;
            this.editing = true;
        },

        cancelEdit: function() {
            this.draftValues = null;
            this.editing = false;
        },

        remove: function() {
            api.deleteSetlistForId(this.setlist.id).then((response) => {
                this.$router.push({ name: "Repertoire" });
            });
        }
    },
}

export default SetlistDetail;
</script>
