<template>
    <div class="category-table">
        <table class="table is-hoverable is-fullwidth" v-cloak>
            <thead>
            <tr>
                <th>Category</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="category in props.categories" class="category" :key="category.id">
                <td class="grow">{{ category.name }}</td>
                <td class="p-1">
                    <button class="button is-danger is-inverted is-small"
                            @click="$emit('remove', category)">
                        <span class="icon is-small">
                            <i class="fas fa-times"></i>
                        </span>
                    </button>
                </td>
            </tr>
            </tbody>
            <tfoot>
            <tr>
                <td v-if="!editing">
                    <a @click.prevent="create" href="#" class="card-footer-item">Add...</a>
                </td>
                <td v-else>
                    <div class="field">
                        <div class="control">
                            <input class="input" type="text" v-model="draftValue"/>
                        </div>
                    </div>
                </td>
                <td v-if="!editing"></td>
                <td v-else class="p-1">
                    <button class="button is-primary is-small mt-2"
                            @click="save">
                        <span class="icon is-small">
                            <i class="far fa-save"></i>
                        </span>
                    </button>
                </td>
            </tr>

            </tfoot>
        </table>
    </div>
</template>

<script setup lang="ts">
import { PropType, ref } from "vue";
import { Category, CategoryType } from "@/types";

const props = defineProps({
    categories: {
        type: Object as PropType<Array<Category>>,
        required: true
    },
    categoryType: {
        type: Object as PropType<CategoryType>,
        required: true
    }
})
const emit = defineEmits(["remove", "save"])

// state
const draftValue = ref<String | undefined>(undefined)
const editing = ref(false)

// methods
const create = () => {
    editing.value = true;
    draftValue.value = "";
}

const save = () => {
    emit("save", {
        name: draftValue.value,
        type: props.categoryType
    });
    editing.value = false;
    draftValue.value = undefined;
}

</script>

<style scoped>
td.grow {
    width: 99%;
}

</style>
