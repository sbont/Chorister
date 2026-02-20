import { defineStore } from "pinia";
import { inject, ref } from "vue";
import { ReadingsServiceKey } from "./readings-service";
import { Readings } from "@/entities/reading";

export type ReadingsState = Readings | "loading" | null;

export const useReadings = defineStore("readings", () => {
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const service = inject(ReadingsServiceKey)!;

    // state
    const readingsByDate = ref(new Map<Date, ReadingsState>);

    // actions
    async function load(date: Date): Promise<Readings> {
      const cached = readingsByDate.value.get(date);
      if (cached && cached !== "loading") {
        return cached;
      }

      readingsByDate.value.set(date, "loading");
      const readings = await service.getReadingsByDate(date);
      readingsByDate.value.set(date, readings);

      return readings;
    }

    return { readingsByDate, load }
}
);
