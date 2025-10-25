import { Entity, EntityRef } from "./entity";
import { Rite } from "./rite";

export type OrderOfService = Entity & {
    id: number;
    name: string;
    rite: EntityRef<Rite>;
}