import { ApiEntityIn, Identifiable } from "."
import { Choir, toDomainChoir } from "./choir"
import { User } from "./user"
import { Invite as DomainInvite } from "@/entities/invite"

export interface Invite {
    email?: string
    token: string
    choir: Choir
}

export interface SingleInvite extends Invite, Identifiable, ApiEntityIn {
    id: number
    email: string
    invitedBy: User
    token: string
    createdDate: Date
    acceptedDate: Date
    expired: Boolean
    choir: Choir
}

export function toDomainInvite(invite: Invite): DomainInvite {
    return {
        token: invite.token,
        choir: toDomainChoir(invite.choir)
    }
}
