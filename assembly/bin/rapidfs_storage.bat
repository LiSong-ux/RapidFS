@echo off
cd ../lib
set command=%1
if %command%==start (
    java -jar -Djava.ext.dirs=./;$JAVA_HOME/jre/lib/ext storage-0.2.0.jar
) else (
    echo Command Error
)
pause