<template>
    <div
        class="add-row has-background-light columns is-vcentered"
        @mouseover="
            () => {
                if (state == State.Ready) state = State.MouseOver;
            }
        "
        @mouseleave="
            () => {
                if (state == State.MouseOver) state = State.Ready;
            }
        "
    >
        <div v-if="state == State.Ready" class="column">
            <span class="icon">
                <i class="fas fa-plus"></i>
            </span>
            Add
        </div>

        <template v-if="state == State.MouseOver" class="">
            <div class="add-btn column is-half has-background-info-light" @click="addSong">
                <span class="icon">
                    <i class="fas fa-plus"></i>
                </span>
                Add song
            </div>
            <div class="add-btn column is-half has-background-primary-light" @click="addHeader">
                <span class="icon">
                    <i class="fas fa-plus"></i>
                </span>
                Add header
            </div>
        </template>
        
        <div v-if="state == State.AddSong || state == State.AddHeader" class="form level column">
            <div class="level-left ml-sixp">
                <div v-if="state == State.AddSong" class="form">
                    <Select
                        v-model="selectedSongId"
                        filter
                        :options="allSongs"
                        option-label="title"
                        option-value="id"
                        placeholder="Select song..."
                        class="selector mr-2"
                    />
                </div>
                
                <div v-if="state == State.AddHeader" class="form">
                    <div class="field is-horizontal">
                        <div class="field-body mr-2">
                            <div class="field">
                                <div class="control">
                                    <input v-model="headerName" class="input" type="text" placeholder="Header"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <button class="button is-primary" @click="save">Add</button>
            </div>
            
            <button class="button is-light level-right" @click="cancel">
                <span class="icon is-small">
                    <i class="fas fa-times"></i>
                </span>
            </button>
        </div>

        
    </div>
</template>

<script setup lang="ts">
import { useEvents } from "@/application/eventStore";
import { useSongs } from "@/application/songStore";
import { ref } from "vue";
import Select from "primevue/select";
import { storeToRefs } from "pinia";

const props = defineProps<{ eventId: number }>();

enum State {
    Ready,
    MouseOver,
    AddSong,
    AddHeader,
}

const state = ref<State>(State.Ready);
const selectedSongId = ref<number>();
const headerName = ref<string>();

const eventStore = useEvents();
const songStore = useSongs();
songStore.fetchAll();
const { allSongs } = storeToRefs(songStore);

// Methods
const addHeader = () => {
    state.value = State.AddHeader;
};
const addSong = () => {
    state.value = State.AddSong;
};

const save = async () => {
    if (state.value == State.AddSong && selectedSongId.value) {
        await eventStore.addEventEntry(props.eventId, { songId: selectedSongId.value });
    } else if (state.value == State.AddHeader && headerName.value) {
        await eventStore.addEventEntry(props.eventId, { label: headerName.value });
    }
    state.value = State.Ready;
};

const cancel = () => (state.value = State.Ready);
</script>

<style>
.add-row {
    height: 64px;
    text-align: center;
    width: 100%;
}
.add-row.columns {
    margin: 0;
}
.add-btn {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.selector {
    min-width: 25rem;
}

.form {
    display: flex;
    align-items: center;
}

.ml-sixp {
    margin-left: 6%;
}
</style>
