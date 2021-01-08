#!/bin/sh
# shellcheck disable=SC2164
cd ../lib
command=$1;
# shellcheck disable=SC2077
# shellcheck disable=SC2140
if [ "$command"=="start" ]; then
nohup java -jar -Djava.ext.dirs=./:"$JAVA_HOME"/jre/lib/ext storage-0.2.0.jar > ../logs/storage.out 2>&1 &
else echo "Command Error"
fi
