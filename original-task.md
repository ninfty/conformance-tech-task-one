# Simple API

Inside the *api* folder is a simple Spring Boot API which exposes two endpoints

```
/api/now
/api/random
```

The /api/now endpoint returns a JSON object with the current time in. The /api/random endpoint
returns a JSON object with a random number in it.

These APIs can be accessed without any authorization. If you run the ApiApplication class in your IDE, you may use 
curl or a similar tool to access these endpoints

```shell
curl http://localhost:8080/api/now
curl http://localhost:8080/api/random
```

Your task is to protect these endpoints using OAuth2. Provided is a very simple authorisation server which you can run 
by simply executing

```shell
docker compose up
```

## The included auth server

This is a heavily stripped down implementation of OAuth2 which only covers absolutely bare essentials for use in this
task. You can check out the discovery document at http://localhost:8081/.well-known/openid-configuration

Additionally, it is configured with a single oauth client, with the following details

```shell
client_id: client1
client secret: abcde12345
```

You will see from the discovery document that token introspection is available. For the sake of simplicity, this endpoint
is unprotected. That is, you will not need to authenticate calls to it. The auth server will **only** issue opaque tokens,
there is no support for JWT tokens. Bear this in mind when creating your solution.

It should not be necessary at all to modify the auth server code to complete this task. However, if you feel there *is*
a need to modify it, explain why you felt it necessary, and be prepared to be questioned on this.

## How to solve

You may implement the solution however you wish. You can utilise existing libraries, Spring Security etc. for this.
You may roll your own solution by hand, either within the API application code, or some other way if you prefer.
