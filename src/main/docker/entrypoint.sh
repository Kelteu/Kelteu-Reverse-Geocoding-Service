#!/bin/sh

echo "Start Kelteu-RLS---------------------------------------------"
exec java -jar $JAVA_EXTRA_OPTS -Dspring.config.additional-location=optional:file:$CONFIG_OVERRIDE_DIR -Dspring.profiles.active=$JAVA_SPRING_PROFILES $APP_JAR