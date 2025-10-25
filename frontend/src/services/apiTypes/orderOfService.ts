import { OrderOfService as DomainOrderOfService } from "@/entities/orderOfService";
import { ApiEntityWith, Identifiable, Link, toDomain, untemplated, WithAssociation } from ".";

export interface OrderOfService extends Identifiable, ApiEntityWith<RiteLink> {
    id: number;
    name: string;
}

export interface RiteLink extends WithAssociation {
    rite: Link
}

export function toDomainOrderOfService(orderOfService: OrderOfService): DomainOrderOfService {
    return {
        ...toDomain(orderOfService),
        rite: { uri: untemplated(orderOfService._links!.rite) }
    };
}
