<template>
    <div class="mychoir">
        <div>
            <section class="hero is-medium is-info">
                <div class="hero-body">
                    <p class="subtitle">My choir</p>
                    <p class="title">{{ choir.name }}</p>
                </div>
            </section>
            <div class="p-3" v-if="!loading">

                <div class="tabs">
                    <ul>
                        <li :class="{ 'is-active': activeTab == 'members' }"><a v-on:click="activeTab = 'members'">Members</a></li>
                        <li :class="{ 'is-active': activeTab == 'categories' }"><a v-on:click="activeTab = 'categories'">Categories</a></li>
                    </ul>
                </div>

                <div class="new-members" v-if="activeTab == 'members'">
                    <ChoirMembers :choir="choir"/>
                </div>

                <div class="categories" v-if="activeTab == 'categories'">
                    hoi
                </div>

            </div>
        </div>
    </div>
</template>

<script lang="ts">
import ChoirMembers from "../components/ChoirMembers.vue";
import api from "./../api.js";
import { onMounted, ref } from 'vue'
import moment from "moment";

export default {
    setup () {
        // state
        
        const choir = ref({})
        const editing = ref(false)
        const draftValues = ref(null)
        const loading = ref(true)
        const saving = ref(false)
        const error = ref(null)
        const activeTab = ref("members")

        // Computed

        onMounted(() => {
            api.getChoirs()
            .then((response) => {
                choir.value = response.data[0];
                console.log("Choir loaded: ", response.data[0]);
                loading.value = false
            })
            .catch((error) => {
                console.log(error);
                error.value = "Failed to load choir";
            });
        })

        // methods
        const formatDate = function(date) {
            if (date) {
                return moment(String(date)).format('DD-MM-YYYY');
            }
        }

        const save = function() {
            saving.value = true;
            var choir = draftValues.value;
            if (!choir) {
                return;
            }
            const promise = api.updateChoirForId(choir.id, choir);
            promise.then(() => {
                editing.value = false;
                saving.value = false;
                choir.value = choir;
                draftValues.value = null;
            })
        }

        const edit = function() {
            draftValues.value = this.setlist;
            editing.value = true;
        }

        const cancelEdit = function() {
            draftValues.value = null;
            editing.value = false;
        }

        return { choir, editing, draftValues, loading, saving, error, activeTab,
        formatDate, save, edit, cancelEdit };
    },
    components: {
        ChoirMembers
    }
}

</script>