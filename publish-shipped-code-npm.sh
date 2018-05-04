#!/bin/bash

echo "Start publishing shipped code npms"

# Navigate to n4js-libs folder
cd `dirname $0`/n4js-libs

rm -rf node_modules

# Install dependencies, e.g. n4js-prepare-npm
yarn install

# Run npm task script 'publish-canary'
yarn run publish-canary

echo "End publishing shipped code npms"
