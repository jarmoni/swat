# S.W.A.T
SSH-Web-Application-Terminal

**Note:** This is just a proof of concept

## Goals

Proof of concept:

- Access SSH-server with common Web-Browser using [Websockets](https://tools.ietf.org/html/rfc6455)
- (HTTP-) Security with [JSON-Web-Token](https://jwt.io/)

## Run it

### SSH-server-setup for testing
- Ensure you have [Docker](https://hub.docker.com) installed

```
cd <PROJECT_ROOT_DIR>/docker
docker build -t swat-ssh .
# run container in foreground, auto-cleanup after container-stop
docker run --rm --name=swat --publish=2222:22 swat-ssh
```
The SSH-daemon allows password- and PK-authentication. You can test the login with a regular SSH-client:
```
# pw-login, password: passwd
ssh -p 2222 root@localhost
# pk-login, passphrase: passwd
ssh -i <PROJECT_ROOT_DIR>docker/ssh/id_rsa_4test -p 2222 root@localhost

```
### Run the S.W.A.T. backend

The easiest way to start the application for testing is the *Spring-Boot-Maven-Plugin* (ensure you have [Maven](https://maven.apache.org) installed.

```
cd <PROJECT_ROOT_DIR>
# runs application in foreground, Ctrl+C for termination
mvn spring-boot:run
```

### Connect to backend
- Open <http://localhost:8080>
- Press *Connect* (Settings-defaults already point to SSH-root-account of the (containerized) SSH-server, and to a predefined 'web-user')

## Issues and limitations

As this project is in prototype-state, some functionality is not fully implemented respectively is not available (yet).

### User-management

The *user-database* consists of static map which contains exactly 1 user (*user:passwd*). But since this is encapsulated in an interface, it would be no big thing, to replace this dummy with e.g. an LDAP- or SQL-based implementation. Due to the static nature of the current implementation it is also not possible to add/remove users.

### Security

Event though there is no 'real' user-management, authentication is fully implemented backend-side. Unfortunately the browser-built-in websocket-client does not support headers for websocket-connections, it was necessary to handle WS(S) and 'regular' HTTP(S)-connections in a different way.

TLS is working, but cannot be (easily) used in the test-setup. This is since browsers refuse to open Websocket-connections without trusted certificates (while HTTPS-connections with self-signed-certs only result in a warning).

### Shell Forwarding

One lesson learned during implementation of the SSH-stuff: Shells behave really, really different! The shell-output has to be post-processed to prevent undesired results. This post-processing is only rudimentarily implemented yet. With a regular Ubuntu-box it works not too bad (Bash and ZSH), but there is lot more to do (not to mention other distribution/shell-combinations).

### Frontend

As I am not the 'UI-guy' I tried to keep it as simple as possible. Here are some of the (innumerable) things that should be improved:

- Login-screen
- Input-validation
- Proper state-transitions (enable/disable components,....)
- UI-design in general

Any contribution is appreciated ;-)


