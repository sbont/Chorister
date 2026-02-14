import { Entity } from "./entity";

export interface Reading extends Entity {
    source: string;
    text: string;
}

export interface Readings {
    date: string;
    day: string;
    reading1: Reading;
    psalm: Reading;
    reading2: Reading | null;
    gospelAcclamation: Reading;
    gospelReading: Reading;
    copyright: Copyright;
}

export interface Copyright {
    text: string;
}