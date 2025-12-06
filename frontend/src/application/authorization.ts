import { AccessLevel } from "@/types/access-level";
import { Operation } from "@/types/operations";

export const EntityLevelPermissions = {
    category: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.MANAGER,
        delete: AccessLevel.MANAGER
    },
    categoryType: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.MANAGER,
        update: AccessLevel.MANAGER,
        delete: AccessLevel.MANAGER
    },
    choir: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.VIEWER,
        update: AccessLevel.MANAGER,
        delete: AccessLevel.MANAGER
    },
    chords: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.EDITOR,
        delete: AccessLevel.EDITOR
    },
    event: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.EDITOR,
        delete: AccessLevel.MANAGER
    },
    eventEntry: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.EDITOR,
        delete: AccessLevel.EDITOR
    },
    score: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.EDITOR,
        delete: AccessLevel.EDITOR
    },
    song: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.EDITOR,
        update: AccessLevel.EDITOR,
        delete: AccessLevel.MANAGER
    },
    user: {
        read: AccessLevel.VIEWER,
        create: AccessLevel.MANAGER,
        update: AccessLevel.MANAGER,
        delete: AccessLevel.MANAGER
    }
} satisfies Record<string, Permissions>;

export type EntityType = keyof typeof EntityLevelPermissions;

export type Permissions = Record<Operation, AccessLevel>;