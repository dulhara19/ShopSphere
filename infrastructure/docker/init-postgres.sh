#!/bin/bash
set -e

# Create multiple databases on first startup
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE shopsphere_user_dev;
    CREATE DATABASE shopsphere_inventory;
    CREATE DATABASE shopsphere_orders;
    CREATE DATABASE analytics_db;
EOSQL
