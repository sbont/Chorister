import { Choir } from "./choir"
import { Entity, EntityRef } from "./entity"

export interface Categories {
    season: Array<Category>,
    liturgical: Array<Category>
}

export interface Category extends Entity {
    id: number,
    choir: EntityRef<Choir> | undefined,
    name: string,
    type: CategoryType
}

export enum CategoryType {
    Season = "SEASON",
    Liturgical = "LITURGICAL_MOMENT"
}