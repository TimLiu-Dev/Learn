#!/bin/sh
# entrypoint with exec: java replaces sh and keeps its pid
exec java -jar hook.jar 30
