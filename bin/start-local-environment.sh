#!/bin/bash

## Usage: ./bin/start-local-environment DOCMOSIS_ACCESS_KEY
##
## Options:
##    - DOCMOSIS_ACCESS_KEY: Access key for docmosis development environment.
##
## Start local environment including idam client setup.


# Set variables
COMPOSE_FILE="-f docker-compose-dependencies.yml"
IDAM_URI="http://localhost:5000"
IDAM_USERNAME="idamOwner@hmcts.net"
IDAM_PASSWORD="Ref0rmIsFun"
export DOCMOSIS_ACCESS_KEY=$1

# Start IDAM setup
echo "Starting shared-db..."
docker-compose ${COMPOSE_FILE} up -d shared-db

echo "Starting IDAM(ForgeRock)..."
docker-compose ${COMPOSE_FILE} up -d fr-am
docker-compose ${COMPOSE_FILE} up -d fr-idm

echo "Starting IDAM..."
docker-compose ${COMPOSE_FILE} up -d idam-api \
                                     idam-web-public \
                                     idam-web-admin

echo "Testing IDAM Authentication..."
token=$(./bin/idam-authenticate.sh ${IDAM_URI} ${IDAM_USERNAME} ${IDAM_PASSWORD})
while [ "_${token}" = "_" ]; do
      sleep 60
      echo "idam-api is not running! Check logs, you may need to restart"
      token=$(./bin/idam-authenticate.sh ${IDAM_URI} ${IDAM_USERNAME} ${IDAM_PASSWORD})
done

# Set up IDAM client with services and roles
echo "Setting up IDAM client..."
(./bin/idam-client-setup.sh ${IDAM_URI} services ${token} '{"description": "em", "label": "em", "oauth2ClientId": "webshow", "oauth2ClientSecret": "AAAAAAAAAAAAAAAA", "oauth2RedirectUris": ["http://localhost:8080/oauth2redirect"], "selfRegistrationAllowed": true}')

# Start all other images
echo "Starting dependencies..."
docker-compose ${COMPOSE_FILE} build
docker-compose ${COMPOSE_FILE} up -d service-auth-provider-api \
                                     smtp-server \
                                     rpa-native-pdf-annotator-db \
                                     annotation-api-db \
                                     dm-store-db \
                                     dm-store \
                                     annotation-api \
                                     azure-storage-emulator-azurite \
                                     make-container-call

echo "LOCAL ENVIRONMENT SUCCESSFULLY STARTED"