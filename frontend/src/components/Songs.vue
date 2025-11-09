<template>
    <div class="p-2">
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <DataTable :value="songs" ref="datatable" v-model:selection="selectedRows" size="small" :loading="loading"
            paginator :rows="25" :rowsPerPageOptions="[25, 50, 100]">
            <template #header>
                <div class="is-flex is-justify-content-space-between">
                    <div class="is-flex title">
                        {{ header }}
                    </div>
                    <div class="is-flex is-gap-2">
                        <Button type="button" class="button mr-2" label="Categorise" @click="toggleCategorizeMenu"
                            :disabled="!selectedRows.length" :class="{ 'is-loading': isSavingCategories }"
                            aria-haspopup="true" aria-controls="overlay_tmenu" unstyled></Button>
                        <TieredMenu ref="customizeMenu" id="overlay_tmenu" :model="categorizeMenuEntries" popup />

                        <router-link class="button is-primary" :to="{ name: 'NewSong' }" append tag="button">
                            <span class="icon is-small">
                                <i class="fas fa-plus"></i>
                            </span>
                            <span>Add</span>
                        </router-link>

                    </div>
                </div>
            </template>
            <template #empty v-if="loading == false"> No songs found. </template>

            <Column body-class="col-select" selection-mode="multiple"></Column>
            <Column field="title" header="Title" body-class="col-title" sortable>
                <template #body="slotProps">
                    <router-link :to="{ name: 'Song', params: { id: (slotProps.data as Song).id } }" append
                        class="has-text-weight-semibold">
                        {{ (slotProps.data as Song).title }}
                    </router-link>
                </template>
            </Column>
            <Column field="composer" header="Composer" body-class="col-composer" sortable></Column>
            <Column field="lastEvent.date" header="Last Included" body-class="col-last-played" sortable>
                <template #body="slotProps">
                    {{ slotProps.data.lastEvent ? useDateFormat(slotProps.data.lastEvent?.date, 'DD-MM-YYYY') : '' }}
                </template>
            </Column>
            <Column header="Categories" body-class="col-category">
                <template #body="slotProps">
                    <div class="tags">
                        <span v-for="(category, index) in categoryStore.songCategories(slotProps.data.id)"
                            class="song-category tag is-normal" :key="index">
                            {{ category.name }}
                        </span>
                    </div>
                </template>
            </Column>
            <template #footer>
                <strong>{{ songs.length }}</strong> {{ pluralize(songs.length) }}
            </template>
        </DataTable>
    </div>
</template>

<script setup lang="ts">
import { useCategories } from '@/application/categoryStore';
import { StoreState } from '@/application/entityStore';
import { useSongs } from "@/application/songStore.js";
import { Category } from '@/entities/category';
import { Song } from "@/entities/song";
import { useDateFormat } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import Button from "primevue/button";
import Column from "primevue/column";
import DataTable from "primevue/datatable";
import type { MenuItem, MenuItemCommandEvent } from 'primevue/menuitem';
import TieredMenu from "primevue/tieredmenu";
import { computed, ref } from 'vue';
import { useRoute } from "vue-router";

// Types
const songStore = useSongs();
const { allSongs, isLoading } = storeToRefs(songStore);
const categoryStore = useCategories();
const { categoriesByType } = storeToRefs(categoryStore);

const route = useRoute();
const customizeMenu = ref();

// state
songStore.initialize();
categoryStore.initialize();
const header = ref("All songs");
const isSavingCategories = ref(false);
const error = ref<string | null>(null);
const datatable = ref();

var songs = ref<Array<Song>>([]);
const loadingCategorySongs = ref(false);
const loading = computed(() => loadingCategorySongs.value || isLoading.value );

switch (route.name) {
    
    case "Repertoire":
        songs = allSongs;
        break;

    case "Category":
        loadingCategorySongs.value = true;
        const categoryId = Number(route.params.id);
        categoryStore.get(categoryId).then((data) => {
            header.value = data?.name ?? "Category";
        });
        songStore.fetchAllForCategory(categoryId).then(data => {
            songs.value = data;
        }).finally(() => (loadingCategorySongs.value = false));
        break;
}

const categorizeMenuEntries = computed(() => {
    if (categoriesByType.value.size) {
        return Array.from(categoriesByType.value, ([typeUri, categories]) => {
            return {
                label: categoryStore.categoryTypes.get(typeUri)?.name,
                items: categories.map(c => ({ 
                    label: c.name, 
                    key: c.uri ,
                    command: categorizeFn(c)
                })),
            } as MenuItem;
        });
    } else {
        return [{
            label: 'Loading...',
            icon: 'fas fa-spinner fa-pulse'
        }];
    }
});

const selectedRows = ref<Song[]>([]);


// Methods
const pluralize = function (n: number) {
    return n === 1 ? "song" : "songs";
}

const handleErrorClick = function () {
    error.value = null;
}

const toggleCategorizeMenu = (event: Event) => {
    customizeMenu.value.toggle(event);
}

const categorizeFn = (category: Category) => {
    return async (_: MenuItemCommandEvent) => {
        isSavingCategories.value = true;
        try {
            await categoryStore.addForSongs(selectedRows.value, category);
            selectedRows.value = [];
        } catch(e) {
            console.log(e);
            error.value = `${error}`;
        }
        isSavingCategories.value = false;
    }
} 

</script>

<style>
[v-cloak] {
    display: none;
}

.song-table {
    table-layout: fixed;
}

.p-datatable-table-container {
    overflow: visible !important;
}

td.p-1b {
    padding: 0.3em;
}

.col-select {
    width: 3%;
}

.col-no {
    width: 3%;
}

.col-title {
    width: 25%;
}

.col-composer {
    width: 20%;
}

.col-songbook {
    width: 15%;
}

.col-songbook-no {
    width: 5%;
}

.col-last-played {
    width: 10%;
}

.col-category {
    width: 32%;
    max-width: 40%;
}

.col-category .tags {
    flex-wrap: initial;
}
</style>