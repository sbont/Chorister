import { Choir } from "./choir"
import { Entity, EntityRef } from "./entity"

export interface CategoryType extends Entity {
    id: number,
    choir: EntityRef<Choir> | undefined,
    name: string,
}
