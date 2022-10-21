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
                
                <div class="invites mt-5">
                    <h4 class="title is-4">Open invites</h4>
                    <table
                        class="table is-hoverable is-fullwidth"
                        v-if="!loading"
                        v-cloak
                    >
                        <thead>
                            <th>Email</th>
                            <th>Date sent</th>
                            <th class="has-text-right">Revoke</th>
                        </thead>
                        <tbody>
                            <tr v-for="invite in invites" class="invite" :key="invite.id">
                                <td>{{ invite.email }}</td>
                                <td>{{ formatDate(invite.createdDate) }}</td>
                                <td class="has-text-right">
                                    <button class="button is-small is-danger is-inverted" @click="revokeInvite(invite)">
                                        <span class="icon is-small">
                                            <i class="fas fa-times"></i>
                                        </span>
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table>
                </div>

                <div class="new-invite mt-5">
                    <h4 class="title is-4">Invite someone</h4>
                    <button class="button is-info mb-3" @click="generateToken" v-if="inviteLink == null">
                        Create invite link
                    </button>

                    <div class="is-flex mb-3" v-if="inviteLink">
                        <div class="control has-icons-right is-flex-grow-1 mr-2" @click="copyToken">
                            <input class="button input" v-on:focus="$event.target.select()" ref="token" readonly :value="inviteLink"/>
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

                    <div class="field">
                        <label class="label">Email</label>
                        <div class="control has-icons-left">
                            <input class="input" type="email" placeholder="hello@email.com">
                            <span class="icon is-small is-left">
                              <i class="fas fa-envelope"></i>
                            </span>
                        </div>
                    </div>
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
            inviteLink: null,
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
                if (this.choir.inviteToken) {
                    let baseUrl = window.location.origin;
                    this.inviteLink = baseUrl + "/signup?invite=" + this.choir.inviteToken;
                }
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
                let allInvites = response.data;
                this.invites = allInvites.filter(i => !i.expired);
            })
            .catch((error) => {
                this.$log.debug(error);
                this.error = "Failed to load invites";
            });
        Promise.allSettled([choirLoaded, membersLoaded, invitesLoaded]).then(()=> this.loading = false)
    },

    methods: {
        formatDate: function(date) {
            if (date) {
                return moment(String(date)).format('DD-MM-YYYY');
            }
        },

        save: function() {
            this.saving = true;
            var choir = this.draftValues;
            if (!choir) {
                return;
            }
            const promise = api.updateChoirForId(choir.id, choir);
            promise.then(() => {
                this.editing = false;
                this.saving = false;
                this.choir = choir;
                this.draftValues = null;
            })
        },

        edit: function() {
            this.draftValues = this.setlist;
            this.editing = true;
        },

        cancelEdit: function() {
            this.draftValues = null;
            this.editing = false;
        },

        generateToken: function() {
            api.getToken()
                .then((response) => {
                    console.log(response);
                    this.inviteLink = response.data;
                });
        },

        deleteToken: function() {
            api.deleteToken()
                .then(() => this.inviteLink = null);
        },

        copyToken: function () {
            this.$refs.token.focus();
            document.execCommand('copy');
        },

        revokeInvite: function(invite) {
            console.log(invite);
            this.invites = this.invites.filter(i => i !== invite);
            invite.expired = true;
            api.updateInviteForId(invite.id, invite);
        }
    },
}

export default MyChoir;
</script>