FROM openjdk:8-jre-alpine

RUN apk update
RUN apk upgrade
RUN apk add bash

COPY target/universal/stage/bin /kortglad/bin
COPY target/universal/stage/lib /kortglad/lib

ENTRYPOINT ["/kortglad/bin/kortglad"]

