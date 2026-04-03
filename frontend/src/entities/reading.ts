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
    psalm2: Reading | null;
    reading3: Reading | null;
    psalm3: Reading | null;
    reading4: Reading | null;
    psalm4: Reading | null;
    reading5: Reading | null;
    psalm5: Reading | null;
    reading6: Reading | null;
    psalm6: Reading | null;
    reading7: Reading | null;
    psalm7: Reading | null;
    reading8: Reading | null;
    psalm8: Reading | null;
    gospelAcclamation: Reading | null;
    gospelReading: Reading;
    copyright: Copyright;
}

export interface Copyright {
    text: string;
}