import { Readings } from "@/entities/reading";
import { InjectionKey } from "vue";

export interface ReadingsService {
  getReadingsByDate: (date: Date) => Promise<Readings>;
}

export const ReadingsServiceKey: InjectionKey<ReadingsService> = Symbol("rs");
