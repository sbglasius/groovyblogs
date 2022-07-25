FROM openjdk:17-alpine

# set environment options
ENV JAVA_OPTS="-Xms64m -Xmx256m -XX:MaxMetaspaceSize=128m"

RUN apk -U update && apk add netcat-openbsd && apk add shadow

RUN mkdir -p /app
WORKDIR /app

COPY /app/groovyblogs.jar groovyblogs.jar
COPY /app/docker-entrypoint.sh docker-entrypoint.sh

RUN ls -la /app

# Set file permissions
RUN chmod a+x /app/docker-entrypoint.sh

# Add unprivileged user
#RUN groupadd -g 1999 appuser && useradd -r -u 1999 -g appuser appuser
#USER appuser

# Set start script as enrypoint
ENTRYPOINT ["/app/docker-entrypoint.sh"]
