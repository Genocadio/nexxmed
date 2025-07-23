#!/bin/bash
set -e

function create_db_if_not_exists() {
  DB_NAME=$1
  echo "Checking if database '$DB_NAME' exists..."
  DB_EXISTS=$(psql -U "$POSTGRES_USER" -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'")

  if [ "$DB_EXISTS" != "1" ]; then
    echo "Creating database '$DB_NAME'..."
    createdb -U "$POSTGRES_USER" "$DB_NAME"
  else
    echo "Database '$DB_NAME' already exists. Skipping."
  fi
}

create_db_if_not_exists auth_service
create_db_if_not_exists user_service
create_db_if_not_exists logging_service
create_db_if_not_exists medicine
create_db_if_not_exists catalog
create_db_if_not_exists inventory
create_db_if_not_exists  medicalemr
create_db_if_not_exists medadmin
create_db_if_not_exists cavgobooks
