#!/bin/bash

# creds for db on heroku
export NIPT_DB_URL="jdbc:postgresql://ec2-54-228-213-36.eu-west-1.compute.amazonaws.com:5432/d5opkfr54ssciv?user=ynuickhfmlaltm&password=spnk3d98dAUiQvY_2o0qcXW23b&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
export NIPT_DB_USER="ynuickhfmlaltm"
export NIPT_DB_PASS="spnk3d98dAUiQvY_2o0qcXW23b"

./gradlew bootRun --stacktrace
