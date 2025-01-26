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
                        <li :class="{ 'is-active': activeTab == 'members' }">
                            <a v-on:click="activeTab = 'members'">Members</a>
                        </li>
                        <li :class="{ 'is-active': activeTab == 'categories' }">
                            <a v-on:click="activeTab = 'categories'">Categories</a>
                        </li>
                    </ul>
                </div>

                <div class="new-members" v-if="activeTab == 'members'">
                    <ChoirMembers />
                </div>

                <div class="categories" v-if="activeTab == 'categories'">
                    <Categories />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { Choir } from "@/entities/choir";
import ChoirMembers from "../components/ChoirMembers.vue";
import { ref } from "vue";
import Categories from "@/components/Categories.vue";
import { useChoir } from "@/application/choirStore";

// state
const choirStore = useChoir();
const loading = ref(true);
const activeTab = ref("members");
const choir = ref<Choir>();
choirStore.getChoir().then(_ => loading.value = false);

</script>