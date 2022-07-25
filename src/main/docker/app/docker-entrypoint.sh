#!/bin/sh
set -e

>&2 echo "Starting GroovyBlogs"
# wait for the mysql docker to be running
while ! nc -zv db 3306 ; do
  >&2 echo "MySQL is unavailable - sleeping"
  sleep 1
done

sleep 2
>&2 echo "MySql is up - executing command"

if [ -f /etc/app/env.sh ] ; then
    >&2 echo "Executing environment"
    chmod +x /etc/app/env.sh
    . /etc/app/env.sh
fi
>&2 echo "VIRTUAL_HOST: $VIRTUAL_HOST"

exec java ${JAVA_OPTS} -jar groovyblogs.jar $@
