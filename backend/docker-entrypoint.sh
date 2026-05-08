#!/bin/sh
set -e

# Resolve the JDBC URL from either SPRING_DATASOURCE_URL or DATABASE_URL
JDBC_URL=""

if [ -n "$SPRING_DATASOURCE_URL" ]; then
  JDBC_URL="$SPRING_DATASOURCE_URL"

elif [ -n "$DATABASE_URL" ]; then
  case "$DATABASE_URL" in
    jdbc:*) JDBC_URL="$DATABASE_URL" ;;
    postgres://*) JDBC_URL="jdbc:postgresql://${DATABASE_URL#postgres://}" ;;
    postgresql://*) JDBC_URL="jdbc:postgresql://${DATABASE_URL#postgresql://}" ;;
    *) JDBC_URL="$DATABASE_URL" ;;
  esac
fi

if [ -n "$JDBC_URL" ]; then
  echo "Starting with PostgreSQL datasource"
  exec java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.datasource.url="$JDBC_URL" \
    -Dspring.datasource.driver-class-name=org.postgresql.Driver \
    -Dspring.datasource.username="" \
    -Dspring.datasource.password="" \
    -jar app.jar
else
  echo "No database URL set — starting with H2 (local dev mode)"
  exec java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -jar app.jar
fi
