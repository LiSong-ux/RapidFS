#!/bin/sh
# shellcheck disable=SC2164
cd ../lib
command=$1;
# shellcheck disable=SC2077
# shellcheck disable=SC2140
if [ "$command"=="start" ]; then
nohup java -Xdebug -Xrunjdwp:transport=dt_socket,suspend=n,server=y,address=5005 \
        -jar -Djava.ext.dirs=./:"$JAVA_HOME"/jre/lib/ext tracker-0.1.0.jar \
        > ../logs/tracker.out 2>&1 &
else echo "Command Error"
fi