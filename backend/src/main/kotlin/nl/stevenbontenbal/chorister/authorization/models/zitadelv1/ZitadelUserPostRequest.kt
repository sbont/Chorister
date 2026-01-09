package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ZitadelUserPostRequest (
    var userName                        : String?         = null,
    var profile                         : Profile?        = null,
    var email                           : Email?          = Email(),
    var phone                           : Phone?          = null,
    var password                        : String?         = null,
    var hashedPassword                  : HashedPassword? = null,
    var passwordChangeRequired          : Boolean?        = null,
    var requestPasswordlessRegistration : Boolean?        = null,
    var otpCode                         : String?         = null,
    var idps                            : ArrayList<Idps> = arrayListOf(),
    var metadata                        : ArrayList<Metadata> = arrayListOf(),
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Profile (
    var firstName         : String? = null,
    var lastName          : String? = null,
    var nickName          : String? = null,
    var displayName       : String? = null,
    var preferredLanguage : String? = null,
    var gender            : String? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Email (
    var email           : String?  = null,
    var isEmailVerified : Boolean? = false
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Phone (
    var phone           : String?  = null,
    var isPhoneVerified : Boolean? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Password (
    var password     : String? = null,
    var changeRequired : Boolean = false
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class HashedPassword (
    var value     : String? = null,
    var algorithm : String? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Idps (
    var configId       : String? = null,
    var externalUserId : String? = null,
    var displayName    : String? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Metadata (
    var key  : String,
    var value: String
)
