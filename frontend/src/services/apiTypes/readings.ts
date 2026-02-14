import { Reading as DomainReading, Readings as DomainReadings } from "@/entities/reading";

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

export interface Reading {
    source: string;
    text: string;
}

export interface Copyright {
    text: string;
}

// Conversion functions

export const toDomainReading = (reading: Reading): DomainReading => {
    return {
        source: reading.source,
        text: reading.text
    };
};

export const toDomainReadings = (readings: Readings): DomainReadings => {
    return {
        date: readings.date,
        day: readings.day,
        reading1: toDomainReading(readings.reading1),
        psalm: toDomainReading(readings.psalm),
        reading2: readings.reading2 ? toDomainReading(readings.reading2) : null,
        gospelAcclamation: toDomainReading(readings.gospelAcclamation),
        gospelReading: toDomainReading(readings.gospelReading),
        copyright: {
            text: readings.copyright.text
        }
    };
};