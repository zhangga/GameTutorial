#!/bin/bash

CURDIR=$(cd $(dirname $0); pwd)
if [ "X$1" != "X" ]; then
    RUNTIME_ROOT=$1
else
    RUNTIME_ROOT=${CURDIR}
fi
echo $CURDIR
files=(*.jar)

JAVA_PARAMS="-Xms512M -Xmx1024M"
JAVA_PARAMS="$JAVA_PARAMS -XX:+UseG1GC -XX:MaxGCPauseMillis=100"
JAVA_PARAMS="$JAVA_PARAMS -XX:ErrorFile=/opt/log/run/hs_err_%p.log -XX:HeapDumpPath=/opt/log/run/heap_dump_err_%p.hprof"
JAVA_PARAMS="$JAVA_PARAMS -XX:+HeapDumpOnOutOfMemoryError"

exec java $JAVA_PARAMS -Dio.netty.tryReflectionSetAccessible=true --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -jar $RUNTIME_ROOT/${files[0]} -c $CURDIR/conf &