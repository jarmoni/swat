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






