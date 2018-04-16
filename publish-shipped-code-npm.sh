#!/bin/bash

# Navigate to shipped-code folder
cd `dirname $0`/plugins/org.eclipse.n4js.external.libraries/shipped-code

rm -rf node_modules

# Install dependencies, e.g. n4js-prepare-npm
yarn install

# Run npm task script 'publish-canary'
yarn run publish-canary
