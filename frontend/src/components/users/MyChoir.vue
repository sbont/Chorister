<template>
    <div class="mychoir">
        <div>
            <DetailHeader v-if="choir" :title="choir.name" subtitle="My choir" subtitle-on-top entity="choir"/>

            <div v-if="!loading" class="p-3">
                <div class="tabs">
                    <ul>
                        <li :class="{ 'is-active': activeTab == 'members' }">
                            <a @click="activeTab = 'members'">Members</a>
                        </li>
                        <li :class="{ 'is-active': activeTab == 'categories' }">
                            <a @click="activeTab = 'categories'">Categories</a>
                        </li>
                    </ul>
                </div>

                <div v-if="activeTab == 'members'" class="new-members">
                    <ChoirMembers />
                </div>

                <div v-if="activeTab == 'categories'" class="categories">
                    <Categories />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { Choir } from "@/entities/choir";
import ChoirMembers from "./ChoirMembers.vue";
import { ref } from "vue";
import Categories from "./Categories.vue";
import { useChoir } from "@/application/choirStore";
import DetailHeader from "../ui/DetailHeader.vue";

// state
const choirStore = useChoir();
const loading = ref(true);
const activeTab = ref("members");
const choir = ref<Choir>();
choirStore.getChoir().then(value => {
    choir.value = value;
    loading.value = false;
});

</script>