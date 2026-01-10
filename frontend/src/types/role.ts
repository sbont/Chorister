export type Role = "ADMIN" | "MANAGER" | "EDITOR" | "VIEWER";

export function equalsOrGreaterThan(role: Role, compareTo: Role): boolean {
    const thisOrd = toOrdinal(role);
    const compareToOrd = toOrdinal(compareTo);

    return thisOrd >= compareToOrd;
}

function toOrdinal(role: Role): number {
    switch (role) {
        case "ADMIN": return 9;
        case "MANAGER": return 3;
        case "EDITOR": return 2;
        case "VIEWER": return 1;
        default: return 0;
    }
}