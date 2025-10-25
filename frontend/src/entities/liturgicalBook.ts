import { EntityRef } from "./entity";
import { Rite } from "./rite";

export type LiturgicalBook = {
    id: number;
    code: string;
    title: string;
    rite: EntityRef<Rite>;
}