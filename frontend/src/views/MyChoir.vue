<template>
    <div class="mychoir">
        <div>
            <section class="hero is-medium is-info">
                <div class="hero-body">
                    <p class="subtitle">My choir</p>
                    <p class="title">{{ choir?.name }}</p>
                </div>
            </section>
            <div class="p-3" v-if="!loading">

                <div class="tabs">
                    <ul>
                        <li :class="{ 'is-active': activeTab == 'members' }"><a
                            v-on:click="activeTab = 'members'">Members</a></li>
                        <li :class="{ 'is-active': activeTab == 'categories' }"><a
                            v-on:click="activeTab = 'categories'">Categories</a></li>
                    </ul>
                </div>

                <div class="new-members" v-if="activeTab == 'members' && choir">
                    <ChoirMembers :choir="choir"/>
                </div>

                <div class="categories" v-if="activeTab == 'categories' && choir">
                    <Categories :choir="choir"/>
                </div>

            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import ChoirMembers from "../components/ChoirMembers.vue";
import api from "./../api.js";
import { onMounted, ref } from 'vue'
import moment from "moment";
import { Choir } from "@/types";
import Categories from "@/components/Categories.vue";

// state
const choir = ref<Choir>()
const editing = ref(false)
const draftValues = ref<Choir | undefined>()
const loading = ref(true)
const saving = ref(false)
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
const formatDate = function (date: Date) {
    if (date) {
        return moment(String(date)).format('DD-MM-YYYY');
    }
}

const save = function () {
    saving.value = true;
    var changedChoir = draftValues.value;
    if (!changedChoir) {
        return;
    }
    const promise = api.updateChoirForId(changedChoir.id, changedChoir);
    promise.then(() => {
        editing.value = false;
        saving.value = false;
        choir.value = changedChoir;
        draftValues.value = undefined;
    })
}


</script>