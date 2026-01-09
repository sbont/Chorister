import { Role } from "@/types/role";
import { Operation } from "@/types/operations";

export const EntityLevelPermissions = {
    category: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.MANAGER,
        delete: Role.MANAGER
    },
    categoryType: {
        read: Role.VIEWER,
        create: Role.MANAGER,
        update: Role.MANAGER,
        delete: Role.MANAGER
    },
    choir: {
        read: Role.VIEWER,
        create: Role.VIEWER,
        update: Role.MANAGER,
        delete: Role.MANAGER
    },
    chords: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.EDITOR,
        delete: Role.EDITOR
    },
    event: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.EDITOR,
        delete: Role.MANAGER
    },
    eventEntry: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.EDITOR,
        delete: Role.EDITOR
    },
    score: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.EDITOR,
        delete: Role.EDITOR
    },
    song: {
        read: Role.VIEWER,
        create: Role.EDITOR,
        update: Role.EDITOR,
        delete: Role.MANAGER
    },
    user: {
        read: Role.VIEWER,
        create: Role.MANAGER,
        update: Role.MANAGER,
        delete: Role.MANAGER
    }
} satisfies Record<string, Permissions>;

export type EntityType = keyof typeof EntityLevelPermissions;

export type Permissions = Record<Operation, Role>;