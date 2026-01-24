<template>
    <section class="hero is-info">
        <div class="hero-body">
            <div class="is-flex is-flex-wrap-wrap is-gap-1">
                <div class="is-flex is-flex-grow-1 is-flex-direction-column is-justify-content-center">
                    <p v-if="subtitle && subtitleOnTop" class="subtitle">
                        {{ mode == "view" ? subtitle : "&nbsp;" }}
                    </p>
                    <p class="title">
                        {{ mode == "view" ? title : mode == "create" ? "Create new" : "Edit" }}
                    </p>
                    <p v-if="mode == 'view' && subtitle && !subtitleOnTop" class="subtitle">
                        {{ subtitle }}
                    </p>
                </div>
                <div class="is-flex is-flex-grow-1 is-justify-content-flex-end is-align-items-center	 is-gap-0-5">
                    <div v-if="mode == 'view'" class="contents">
                        <div v-for="button in accessibleActions" :key="button.label" class="control">
                            <button class="button is-link is-inverted" @click="button.action">
                                {{ button.label }}
                            </button>
                        </div>
                    </div>
                    
                    <div v-if="!editDisabled && mode == 'view' && authStore.userCan('update', entity)" class="control">
                        <button 
                            class="button is-link is-inverted" 
                            :disabled="editDisabled"
                            @click="emit('edit')">
                            Edit
                        </button>
                    </div>
                    <div v-if="mode == 'edit'" class="control">
                        <button class="button" @click="emit('cancelEdit')">Cancel</button>
                    </div>
                    <div v-if="mode == 'edit' && authStore.userCan('update', entity)" class="control">
                        <button 
                            class="button" 
                            :class="{ 'is-loading': saving, 'is-link is-inverted': !saving }"
                            :disabled="editDisabled"
                            @click="emit('save')">
                            Save
                        </button>
                    </div>
                    <div v-if="!deleteDisabled && mode == 'view' && authStore.userCan('delete', entity)" class="control">
                        <button 
                            class="button is-danger is-inverted" 
                            :disabled="deleteDisabled"
                            @click="confirmDelete()">
                            Delete
                        </button>
                    </div>
            </div>
        </div>
    </div>
</section>
<ConfirmDialog key="delete"></ConfirmDialog>
</template>

<script setup lang="ts">
    import { EntityType } from '@/application/authorization';
    import { useAuth } from '@/application/authStore';
    import { PageMode } from '@/types';
    import { Role } from '@/types/role';
    import { storeToRefs } from 'pinia';
    import { useConfirm } from 'primevue/useconfirm';
    import ConfirmDialog from 'primevue/confirmdialog';
    
    const emit = defineEmits(["edit", "delete", "cancelEdit", "save"]);
    
    interface Props {
        title?: string
        subtitle?: string
        subtitleOnTop?: boolean
        mode?: PageMode
        entity: EntityType
        editDisabled?: boolean
        deleteDisabled?: boolean
        saving?: boolean
        customActions?: Array<{ action: () => void, label: string, disabled?: boolean, accessLevel: Role }>
    }
    
    const props = withDefaults(defineProps<Props>(), {
        subtitleOnTop: () => false,
        mode: "view",
        editDisabled: false,
        deleteDisabled: false
    });
    
    const authStore = useAuth();
    const { role } = storeToRefs(authStore);
    
    const confirm = useConfirm();
    
    const accessibleActions = props.customActions?.filter(action => !role.value || action.accessLevel <= role.value);
    
    function confirmDelete() {
        confirm.require({
            message: 'Are you sure you want to delete this record?',
            header: 'Confirm deletion',
            icon: 'pi pi-exclamation-triangle',
            rejectProps: {
                label: 'Cancel',
                severity: 'secondary',
                outlined: true
            },
            acceptProps: {
                label: 'Delete',
                severity: 'danger'
            },
            accept: () => {
                emit('delete');
            },
            reject: () => { },
            group: "dialogs"
        });
    }
    
</script>
<style lang="css">
.mx--1 {
    margin-left: -0.25rem;
    margin-right: -0.25rem;
}

.contents {
    display: contents;
}

.is-gap-0-5 {
    gap: 0.5rem;
}

.is-gap-1 {
    gap: 1rem;
}

@media (max-width: 600px) {
    .hero-body {
        padding: 1.5rem 1.5rem;
    }

    .title {
        font-size: 1.5rem;
    }
}

</style>