#!/bin/bash

ROOT_DIR=/opt/apps/fapweb-0.1.0-SNAPSHOT

pid=$ROOT_DIR/RUNNING_PID
if [ -f $pid ]; then
  kill -9 `cat $pid`
  rm -f $pid
fi


${ROOT_DIR}/bin/fapweb \
    -Dhttp.port=9005 \
    -J-Xmx1280M