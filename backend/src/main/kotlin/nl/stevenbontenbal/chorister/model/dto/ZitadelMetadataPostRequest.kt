package nl.stevenbontenbal.chorister.model.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ZitadelMetadataPostRequest (
    var value: String
)
