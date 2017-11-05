# S.W.A.T
SSH-Web-Application-Terminal

**Note:** This is just a proof of concept

## Goals

- Access SSH-server via common Web-Browser
- Security

## Run it
- Ensure you have *Docker* installed
- The following command runs an *Ubuntu-minimal*-container
```
docker run --rm --publish=2222:22 rastasheep/ubuntu-sshd:16.04
```
- Start *S.W.A.T.* (default-port is 8080 - without TLS)
- Start browser and open <localhost:8080>
- Press *Connect* (Settings-defaults already point to SSH-root-account of the docker-image)




