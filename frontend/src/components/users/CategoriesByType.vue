<template>
    <div class="category-table">
        <table v-cloak class="table is-hoverable is-fullwidth">
            <thead>
                <tr>
                    <th>Category</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="category in props.categories" :key="category.id" class="category">
                    <td class="grow">{{ category.name }}</td>
                    <td class="p-1">
                        <button v-if="authStore.userCan('delete', 'category')" class="button is-danger is-inverted is-small" @click="$emit('remove', category)">
                            <span class="icon is-small">
                                <i class="fas fa-times"></i>
                            </span>
                        </button>
                    </td>
                </tr>
            </tbody>
            <tfoot>
                <tr v-if="authStore.userCan('create', 'category')">
                    <td v-if="!editing">
                        <a href="#" class="card-footer-item" @click.prevent="create">Add...</a>
                    </td>
                    <td v-else>
                        <div class="field">
                            <div class="control">
                                <input v-model="draftValue" class="input" type="text" />
                            </div>
                        </div>
                    </td>
                    <td v-if="!editing"></td>
                    <td v-else class="p-1">
                        <button class="button is-primary is-small mt-2" @click="save">
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
import { useAuth } from "@/application/authStore";
import { Category } from "@/entities/category";
import { CategoryType } from "@/entities/categoryType";
import { EntityRef } from "@/entities/entity";
import { ref } from "vue";

const props = defineProps<{
    categoryType: CategoryType,
    categories: Array<Category>
}>();
const emit = defineEmits(["remove", "save"])

// state
const authStore = useAuth();
const draftValue = ref<string | undefined>(undefined)
const editing = ref(false)

// methods
const create = () => {
    editing.value = true;
    draftValue.value = "";
}

const save = () => {
    emit("save", {
        name: draftValue.value,
        categoryType: new EntityRef(props.categoryType)
    } as Category);
    editing.value = false;
    draftValue.value = undefined;
}

</script>

<style scoped>
td.grow {
    width: 99%;
}
</style>
