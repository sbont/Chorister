export interface Registration {
    displayName: string
    email: string
    password: string
}

export interface NewChoirRegistration extends Registration {
    choirName: string
}

export interface AcceptInvite extends Registration {
    token: string
}