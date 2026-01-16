<template>
    <div class="p-2">
        <div v-if="error" class="error" @click="handleErrorClick">
            ERROR: {{ error }}
        </div>
        <DataTable
            ref="datatable" v-model:selection="selectedRows" :value="songs" size="small" :loading="loading"
            paginator :rows="page.size" :first="first" :rows-per-page-options="[25, 50, 100]" @page="onPaged">
            <template #header>
                <div class="is-flex is-justify-content-space-between">
                    <div class="is-flex title">
                        {{ header }}
                    </div>
                    <div class="is-flex is-gap-2">
                        <Button
                            v-if="authStore.userCan('update', 'song')" type="button" class="button mr-2" label="Categorise"
                            :disabled="!selectedRows.length" :class="{ 'is-loading': isSavingCategories }"
                            aria-haspopup="true" aria-controls="overlay_tmenu" unstyled
                            @click="toggleCategorizeMenu" />
                        <TieredMenu ref="customizeMenu" :model="categorizeMenuEntries" popup />

                        <router-link v-if="authStore.userCan('create', 'song')" class="button is-primary" :to="{ name: 'NewSong' }">
                            <span class=" icon is-small">
                                <i class="fas fa-plus"></i>
                            </span>
                            <span>Add</span>
                        </router-link>

                    </div>
                </div>
            </template>
            <template v-if="loading == false" #empty> No songs found. </template>

            <Column body-class="col-select" selection-mode="multiple"></Column>
            <Column field="title" header="Title" body-class="col-title" sortable>
                <template #body="slotProps">
                    <router-link
                        :to="{ name: 'Song', params: { id: (slotProps.data as Song).id } }"
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
                        <span
                            v-for="(category, index) in categoryStore.songCategories(slotProps.data.id)"
                            :key="index" class="song-category tag is-normal">
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
import { useAuth } from '@/application/authStore';
import { useCategories } from '@/application/categoryStore';
import { useSongs } from "@/application/songStore.js";
import { Category } from '@/entities/category';
import { Song } from "@/entities/song";
import { useDateFormat } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import Button from "primevue/button";
import Column from "primevue/column";
import DataTable, { DataTablePageEvent } from "primevue/datatable";
import type { MenuItem } from 'primevue/menuitem';
import TieredMenu from "primevue/tieredmenu";
import { computed, ref } from 'vue';
import { useRoute, useRouter } from "vue-router";

// Types
const authStore = useAuth();
const songStore = useSongs();
const { allSongs, isLoading } = storeToRefs(songStore);
const categoryStore = useCategories();
const { categoriesByType } = storeToRefs(categoryStore);

const route = useRoute();
const router = useRouter();
const customizeMenu = ref();

// state
songStore.initialize();
categoryStore.initialize();
const header = ref("All songs");
const isSavingCategories = ref(false);
const error = ref<string | null>(null);
const datatable = ref();
const page = computed({
    get() {
        return {
            number: route.query.page ? +route.query.page : 1,
            size: route.query.size ? +route.query.size : 25
        };
    },
    set({ number, size }) {
        router.replace({
            query: {
                page: number,
                size
            }
        })
    },
})
const first = computed(() => (page.value.number - 1) * page.value.size);

var songs = ref<Array<Song>>([]);
const selectedRows = ref<Song[]>([]);
const loadingCategorySongs = ref(false);
const loading = computed(() => loadingCategorySongs.value || isLoading.value);

switch (route.name) {
    case "Repertoire": {
        // eslint-disable-next-line vue/no-ref-as-operand
        songs = allSongs;
        break;
    }
    case "Category": {
        loadingCategorySongs.value = true;
        const categoryId = parseInt(route.params.id as string);
        categoryStore.get(categoryId).then((data) => {
            header.value = data?.name ?? "Category";
        });
        songStore.fetchAllForCategory(categoryId).then(data => {
            songs.value = data;
        }).finally(() => (loadingCategorySongs.value = false));
        break;
    }
}

const categorizeMenuEntries = computed(() => {
    if (categoriesByType.value.size) {
        return Array.from(categoriesByType.value, ([typeUri, categories]) => {
            return {
                label: categoryStore.categoryTypes.get(typeUri)?.name,
                items: categories.map(c => ({
                    label: c.name,
                    key: c.uri,
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


// Methods
function pluralize(n: number) {
    return n === 1 ? "song" : "songs";
}

function handleErrorClick() {
    error.value = null;
}

function toggleCategorizeMenu(event: Event) {
    customizeMenu.value.toggle(event);
}

function categorizeFn(category: Category) {
    return async () => {
        isSavingCategories.value = true;
        try {
            await categoryStore.addForSongs(selectedRows.value, category);
        } catch (e) {
            console.log(e);
            error.value = `${error.value}`;
        }
        isSavingCategories.value = false;
    }
}

function onPaged(event: DataTablePageEvent) {
    page.value = { number: event.page + 1, size: event.rows };
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

.p-overlay-mask {
    background: white;
    color: black;
    opacity: 50%;
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