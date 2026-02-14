import { Readings, toDomainReadings } from "./apiTypes/readings";
import BaseService from "./base.service";
import { ReadingsService as IReadingsService } from "../application/readings-service"


export default class ReadingsService extends BaseService implements IReadingsService {
  private path = "readings";

  async getReadingsByDate(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const formattedDate = `${year}${month}${day}`;

    const response = await this.instance.get<Readings>(`${this.path}/${formattedDate}`);
    return toDomainReadings(response.data);
  }
}