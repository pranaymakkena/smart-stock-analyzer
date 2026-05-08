#!/bin/sh
# Converts Render's DATABASE_URL (postgres://...) to Spring's expected format
# (jdbc:postgresql://...) and passes it as a JVM system property.
# This runs before the JVM starts so Hibernate always gets a valid JDBC URL.

set -e

JDBC_URL=""

if [ -n "$DATABASE_URL" ]; then
  # Strip postgres:// or postgresql:// prefix and replace with jdbc:postgresql://
  case "$DATABASE_URL" in
    jdbc:*)
      JDBC_URL="$DATABASE_URL"
      ;;
    postgres://*)
      JDBC_URL="jdbc:postgresql://${DATABASE_URL#postgres://}"
      ;;
    postgresql://*)
      JDBC_URL="jdbc:postgresql://${DATABASE_URL#postgresql://}"
      ;;
    *)
      JDBC_URL="$DATABASE_URL"
      ;;
  esac

  echo "Starting with PostgreSQL: ${JDBC_URL%%@*}@..."

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
  echo "No DATABASE_URL set — starting with H2 (local dev mode)"

  exec java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -jar app.jar
fi
