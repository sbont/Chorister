module.exports = {
    self: "localhost:8080",
    "/api": {
        uri: "localhost:8090",
        preserveHost: true,
        preservePrefix: true
    },
    "/auth": {
        uri: "localhost:9000",
        preserveHost: false,
        preservePrefix: false,
        log: true
    },
    "/": {
        uri: "localhost:8091",
        preserveHost: true
    }
};