import { Operation } from "@/types/operations";
import { Role } from "@/types/role";

export const EntityLevelPermissions = {
    category: {
        read: "VIEWER",
        create: "EDITOR",
        update: "MANAGER",
        delete: "MANAGER"
    },
    categoryType: {
        read: "VIEWER",
        create: "MANAGER",
        update: "MANAGER",
        delete: "MANAGER"
    },
    choir: {
        read: "VIEWER",
        create: "VIEWER",
        update: "MANAGER",
        delete: "MANAGER"
    },
    chords: {
        read: "VIEWER",
        create: "EDITOR",
        update: "EDITOR",
        delete: "EDITOR"
    },
    event: {
        read: "VIEWER",
        create: "EDITOR",
        update: "EDITOR",
        delete: "MANAGER"
    },
    eventEntry: {
        read: "VIEWER",
        create: "EDITOR",
        update: "EDITOR",
        delete: "EDITOR"
    },
    score: {
        read: "VIEWER",
        create: "EDITOR",
        update: "EDITOR",
        delete: "EDITOR"
    },
    song: {
        read: "VIEWER",
        create: "EDITOR",
        update: "EDITOR",
        delete: "MANAGER"
    },
    user: {
        read: "VIEWER",
        create: "MANAGER",
        update: "MANAGER",
        delete: "MANAGER"
    }
} satisfies Record<string, Permissions>;

export type EntityType = keyof typeof EntityLevelPermissions;

export type Permissions = Record<Operation, Role>;