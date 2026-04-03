import { Reading as DomainReading, Readings as DomainReadings } from "@/entities/reading";

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
    psalm2: readings.psalm2 ? toDomainReading(readings.psalm2) : null,
    reading3: readings.reading3 ? toDomainReading(readings.reading3) : null,
    psalm3: readings.psalm3 ? toDomainReading(readings.psalm3) : null,
    reading4: readings.reading4 ? toDomainReading(readings.reading4) : null,
    psalm4: readings.psalm4 ? toDomainReading(readings.psalm4) : null,
    reading5: readings.reading5 ? toDomainReading(readings.reading5) : null,
    psalm5: readings.psalm5 ? toDomainReading(readings.psalm5) : null,
    reading6: readings.reading6 ? toDomainReading(readings.reading6) : null,
    psalm6: readings.psalm6 ? toDomainReading(readings.psalm6) : null,
    reading7: readings.reading7 ? toDomainReading(readings.reading7) : null,
    psalm7: readings.psalm7 ? toDomainReading(readings.psalm7) : null,
    reading8: readings.reading8 ? toDomainReading(readings.reading8) : null,
    psalm8: readings.psalm8 ? toDomainReading(readings.psalm8) : null,
    gospelAcclamation: readings.gospelAcclamation ? toDomainReading(readings.gospelAcclamation) : null,
    gospelReading: toDomainReading(readings.gospelReading),
    copyright: {
      text: readings.copyright.text
    }
  };
};