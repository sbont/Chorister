<template>
    <div class="mychoir">
        <div v-if="!loading">
            <div class="p-3">
                <div class="members">
                    <h4 class="title is-4">Members</h4>
                    <table class="table is-hoverable is-fullwidth" v-if="!loading" v-cloak>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th v-if="role && equalsOrGreaterThan(role, 'MANAGER')">Email</th>
                                <th>Role</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="member in members" class="member" :key="member.id">
                                <th>{{ member.firstName }} {{ member.lastName }}</th>
                                <td v-if="role && equalsOrGreaterThan(role, 'MANAGER')">{{ member.email }}</td>
                                <td>
                                    {{ displayRoles(member.roles) }}
                                    <span v-if="canEditUserRoles(member.id)" class="ml-2">
                                        <button @click="openRoleDialog(member)" class="button is-small is-text">
                                            <span class="icon is-small">
                                                <i class="fas fa-pencil-alt"></i>
                                            </span>
                                        </button>
                                    </span>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
                <div class="new-invite mt-5" v-if="userCan('create', 'user')">
                    <h4 class="title is-4">Invite someone</h4>
                    <button class="button is-info mb-3" @click="generateToken" v-if="inviteLink == undefined">
                        Create invite link
                    </button>

                    <div class="is-flex mb-3" v-if="inviteLink">
                        <div class="control has-icons-right is-flex-grow-1 mr-2" @click="copyToken">
                            <input class="button input" v-on:focus="selectToken" ref="token" readonly
                                :value="inviteLink" />
                            <span class="icon is-small is-right">
                                <i class="fas fa-copy"></i>
                            </span>
                        </div>
                        <button class="button is-danger is-outlined" @click="deleteToken">
                            <span class="icon is-small">
                                <i class="fas fa-times"></i>
                            </span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Role Edit Dialog -->
    <Dialog v-model:visible="roleDialogVisible" header="Edit User Role" modal class="p-fluid">
        <div class="field">
            <label class="label">User</label>
            <div class="control">
                {{ selectedUser ? `${selectedUser.firstName} ${selectedUser.lastName ?? ''}` : '' }}
            </div>
        </div>

        <div class="field">
            <label class="label">Role</label>
            <div class="control">
                <Select v-model="selectedRole" :options="roleOptions" optionLabel="name" optionValue="value"
                    placeholder="Select a role" class="w-full" />
            </div>
        </div>

        <template #footer>
            <Button label="Cancel" icon="pi pi-times" @click="roleDialogVisible = false" class="p-button-text" />
            <Button label="Save" icon="pi pi-check" @click="saveRole" autofocus class="p-button-primary" />
        </template>
    </Dialog>
</template>

<script setup lang="ts">
import { capitalize, computed, ref } from "vue";
import { User } from "@/entities/user";
import { useUsers } from "@/application/userStore.js";
import { useChoir } from "@/application/choirStore";
import { useAuth } from "@/application/authStore";
import { storeToRefs } from "pinia";
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import Select from "primevue/select";
import { Role, equalsOrGreaterThan } from "@/types/role";

// state
const authStore = useAuth();
const { userCan } = authStore;
const { role } = storeToRefs(authStore);
const userStore = useUsers();
const choirStore = useChoir();
const inviteLink = computed(() => {
    if (choirStore.inviteToken == undefined)
        return undefined;

    let baseUrl = window.location.origin;
    return baseUrl + "/signup?invite=" + choirStore.inviteToken;
})

const members = ref<Array<User>>([]);
const token = ref();
const loading = ref(true);
const roleDialogVisible = ref(false);
const selectedUser = ref<User | null>(null);
const selectedRole = ref<Role | null>(null);
const currentUserId = ref<number | null>(null);

const roleOptions = [
    { value: "VIEWER", name: "Viewer" },
    { value: "EDITOR", name: "Editor" },
    { value: "MANAGER", name: "Manager" }
];

userStore
    .fetchAll()
    .then((data) => {
        members.value = data;
    })
    .catch((error) => {
        console.error(error);
        error.value = "Failed to load members";
    })
    .finally(() => (loading.value = false));


function displayRoles(roles: Role[] | undefined) {
    return roles?.map(capitalizeRoleName).join(", ") ?? '';
}

function capitalizeRoleName(role: Role) {
    return capitalize(role.toLocaleLowerCase());
}

function generateToken() {
    return choirStore.generateToken();
}

function deleteToken() {
    return choirStore.deleteToken();
}

function selectToken(event: Event) {
    return (event.target as HTMLInputElement).select();
}

function copyToken() {
    token.value.focus();
    document.execCommand("copy");
}

userStore.getCurrent().then((currentUser) => {
    currentUserId.value = currentUser.id;
});

function canEditUserRoles(userId: number) {
    // User must be a MANAGER and cannot edit their own role
    return role.value === "MANAGER" && userId !== currentUserId.value;
}

function openRoleDialog(user: User) {
    selectedUser.value = user;
    selectedRole.value = user.roles.length > 0 ? user.roles[0] : "VIEWER";
    roleDialogVisible.value = true;
}

async function saveRole() {
    if (selectedUser.value && selectedRole.value) {
        try {
            await userStore.updateUserRoles(selectedUser.value.id, [selectedRole.value]);
            // Refresh the members list
            const updatedMembers = await userStore.fetchAll();
            members.value = updatedMembers;
            roleDialogVisible.value = false;
        } catch (error) {
            console.error('Failed to update user role:', error);
        }
    }
}
</script>