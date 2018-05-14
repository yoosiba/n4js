#!/bin/bash
cd `dirname $0`
cd `pwd -P`

NPM_REGISTRY="http://localhost:4873"

echo "We are currently in $PWD"

# Login as a test user
npm-cli-login -u testuser -p testpass -e test@pass.com -r $NPM_REGISTRY

# Start Verdaccio
verdaccio -l localhost:4873 &

# Publish using canary version
lerna publish --loglevel silly --skip-git --registry="${NPM_REGISTRY}" --force-publish --exact --canary --yes --sort
