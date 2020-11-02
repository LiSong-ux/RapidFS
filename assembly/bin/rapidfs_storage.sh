#!/bin/sh
# shellcheck disable=SC2164
cd ../lib
command=$1;
# shellcheck disable=SC2077
# shellcheck disable=SC2140
if [ "$command"=="start" ]; then
java -jar -Djava.ext.dirs=./:"$JAVA_HOME"/jre/lib/ext storage-0.1.0.jar
else echo "Command Error"
fi
