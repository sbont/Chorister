import { CategoryType } from "./categoryType"
import { Choir } from "./choir"
import { Entity, EntityRef } from "./entity"

export interface Category extends Entity {
    id: number,
    choir: EntityRef<Choir> | undefined,
    name: string,
    categoryType: EntityRef<CategoryType> | undefined,
}
