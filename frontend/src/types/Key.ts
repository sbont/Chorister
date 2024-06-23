export enum Key {
    C = "C",
    DB = "Db",
    D = "D",
    EB = "Eb",
    E = "E",
    F = "F",
    GB = "Gb",
    G = "G",
    AB = "Ab",
    A = "A",
    BB = "Bb",
    B = "B",
}

export const KeyLabelMapping: Record<Key, string> = {
    [Key.C]: "C",
    [Key.DB]: "C#/D♭",
    [Key.D]: "D",
    [Key.EB]: "D#/E♭",
    [Key.E]: "E",
    [Key.F]: "F",
    [Key.GB]: "F#/G♭",
    [Key.G]: "G",
    [Key.AB]: "G#/A♭",
    [Key.A]: "A",
    [Key.BB]: "A#/B♭",
    [Key.B]: "B",
}