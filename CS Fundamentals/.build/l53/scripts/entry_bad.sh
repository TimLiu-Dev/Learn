#!/bin/sh
# entrypoint without exec: sh stays as the parent of java
java -jar hook.jar 30
