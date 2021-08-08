module.exports = {
    self: "localhost:8080",
    "/api": {
        uri: "localhost:8090",
        preserveHost: true,
        preservePrefix: true
    },
    "/auth/": {
        uri: "localhost:9000",
        preserveHost: true,
        preservePrefix: true
    },
    "/": {
        uri: "localhost:8091",
        preserveHost: true
    }
};