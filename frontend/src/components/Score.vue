<template>
    <div>
        <div v-if="!editing" class="card score m-2">
            <header class="card-header">
                <p class="card-header-title">
                    {{ score.description }}
                </p>
            </header>
            <div class="card-content p-0">
                <div class="content is-flex">
                    <iframe
                        :src="previewUrl"
                        width="318"
                        height="450"
                    ></iframe>
                </div>
            </div>
            <footer class="card-footer">
                <a @click.prevent="edit" href="#" class="card-footer-item">Edit</a>
                <a @click.prevent="remove" href="#" class="card-footer-item has-text-danger">Delete</a>
            </footer>
        </div>
        <div v-if="editing" class="card m-2">
            <div class="card-content">
                <div class="content is-flex is-flex-direction-column">
                    <div class="field">
                        <label class="label">Description</label>
                        <div class="control">
                            <input
                                class="input"
                                type="text"
                                v-model="draftValues.description"
                                placeholder="Version name, instrument, tonality..."
                            />
                        </div>
                    </div>
                    <div class="field">
                        <label class="label">Google Drive URL</label>
                        <div class="control">
                            <input
                                class="input"
                                type="text"
                                v-model="draftValues.fileUrl"
                                placeholder="https://drive.google.com/file/d/..."
                            />
                        </div>
                    </div>
                </div>
            </div>
            <footer class="card-footer">
                <a href="#" class="card-footer-item" @click.prevent="save">
                    Save
                </a>
                <a href="#" class="card-footer-item has-text-danger" @click.prevent="cancelEdit">
                    Cancel
                </a>
            </footer>
        </div>
    </div>
</template>
<script>
import api from "../Api";

// app Vue instance
const Score = {
    name: "Score",
    props: ["score"],

    // app initial state
    data: function () {
        return {
            editing: false,
            draftValues: null,
            saving: false,
            error: null,
        };
    },

    mounted: function () {
        if (!this.score.id) {
            this.draftValues = this.score;
            this.editing = true;
        } 
    },

    computed: {
        isNew: function () {
            return !this.score.id;
        },
        previewUrl: function () {
            return this.score.fileUrl.replace("/view", "/preview");
        }
    },

    methods: {
        edit: function () {
            this.draftValues = this.score;
            this.editing = true;
        },

        save: function () {
            this.score = this.draftValues;
            this.editing = false;
                        console.log(this.score);
            if(this.score.id) {
                api.updateScoreForId(this.score.id, this.score);
            } else {
                api.createNewScore(this.score);
            }
        },

        cancelEdit: function () {
            this.editing = false;
            this.draftValues = null;
            if(!this.score.id) {
                this.$emit("remove");
            }
        },

        remove: function () {
            this.$emit("remove");
        },
    },

};

export default Score;
</script>

<style>
[v-cloak] {
    display: none;
}
.score {
    max-width: 20rem;
}
</style>