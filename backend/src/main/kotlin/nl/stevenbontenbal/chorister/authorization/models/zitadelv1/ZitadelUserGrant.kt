package nl.stevenbontenbal.chorister.authorization.models.zitadelv1

data class ZitadelUserGrant(
    var id: String,
    var details: ZitadelUserGrantDetails,
    var roleKeys: List<String>,
    var state: String,
    var userId: String,
    var userName: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var displayName: String,
    var orgId: String,
    var orgName: String,
    var orgDomain: String,
    var projectId: String,
    var projectName: String,
    var preferredLoginName: String,
    var userType: String,
    var grantedOrgId: String,
    var grantedOrgName: String,
    var grantedOrgDomain: String
)