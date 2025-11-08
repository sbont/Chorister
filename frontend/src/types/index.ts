export type Uri = string;

export type PageMode = "view" | "edit" | "create"

export const enum PageState {
    Loading,
    Ready,
    Editing,
}
