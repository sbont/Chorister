module.exports = {
    self: "localhost:8082",
    "/api": {
        uri: "localhost:8080",
        preserveHost: true,
        preservePrefix: true
    },
    "/": {
        uri: "localhost:8081",
        preserveHost: true
    }
};