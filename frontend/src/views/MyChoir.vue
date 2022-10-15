<template>
    <div class="mychoir">
        <div v-if="!loading">
            <section class="hero is-medium is-info">
                <div class="hero-body">
                    <p class="subtitle">My choir</p>
                    <p class="title">{{ choir.name }}</p>
                </div>
            </section>
            <div class="p-3">
                <div class="members">
                    <h4 class="title is-4">Members</h4>
                    <table
                        class="table is-hoverable is-fullwidth"
                        v-if="!loading"
                        v-cloak
                    >
                        <thead>
                            <th>Name</th>
                            <th>Email</th>
                        </thead>
                        <tbody>
                            <tr v-for="member in members" class="member" :key="member.id">
                                <th>{{ member.displayName }}</th>
                                <td>{{ member.email }}</td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div>
                
                <div class="invites">
                    <h4 class="title is-4">Open invites</h4>
                    <table
                        class="table is-hoverable is-fullwidth"
                        v-if="!loading"
                        v-cloak
                    >
                        <thead>
                            <th>Email</th>
                            <th>Date sent</th>
                        </thead>
                        <tbody>
                            <tr v-for="invite in invites" class="invite" :key="invite.id">
                                <td>{{ invite.email }}</td>
                                <td>{{ invite.createdDate | formatDate }}</td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>

                </div>

            </div>
        </div>
    </div>
</template>

<script>
import api from "../api";
import moment from "moment";

const MyChoir = {
    name: "MyChoir",

    data: function () {
        return {
            choir: {},
            members: [],
            invites: [],
            editing: false,
            draftValues: null,
            loading: true,
            saving: false,
            error: null,
        };
    },

    computed: {
    },

    mounted: function () {
        let choirLoaded = api.getChoirs()
            .then((response) => {
                this.$log.debug("Choirs loaded: ", response.data);
                this.choir = response.data[0];
                this.$log.debug("Choir loaded: ", response.data[0]);
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load choir";
            });
        let membersLoaded = api.getUsers()
            .then((response) => {
                this.$log.debug("Members loaded: ", response.data);
                this.members = response.data;
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load members";
            });
        let invitesLoaded = api.getInvites()
            .then((response) => {
                this.$log.debug("Invites loaded: ", response.data);
                this.invites = response.data;
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load invites";
            });
        Promise.allSettled([choirLoaded, membersLoaded, invitesLoaded]).then(()=> this.loading = false)
    },

    filters: {
        formatDate: function(date) {
            if (date) {
                return moment(String(date)).format('DD-MM-YYYY');
            }
        }
    },

    methods: {
        save: function() {
            this.saving = true;
            var setlist = this.draftValues;
            if (!setlist) {
                return;
            }
            const promise = this.saveToServer(setlist);
            promise.then((response) => {
                if(this.isNew) {
                    setlist.id = response.data.id;
                }
                this.editing = false;
                this.saving = false;
                this.setlist = setlist;
                this.draftValues = null;
                if(this.isNew) {
                    this.$router.push({
                        name: "Setlist",
                        params: { id: setlist.id },
                    });
                } 
            })
        },

        saveToServer: function(setlist) {
            if (setlist.id) {
                return api.updateSetlistForId(setlist.id, setlist);
            } else {
                return api.createNewSetlist(setlist);
            }
        },

        edit: function() {
            this.draftValues = this.setlist;
            this.editing = true;
        },

        cancelEdit: function() {
            this.draftValues = null;
            this.editing = false;
        },

        remove: function() {
            api.deleteSetlistForId(this.setlist.id).then(() => {
                this.$router.push({ name: "Repertoire" });
            });
        }
    },
}

export default MyChoir;
</script>