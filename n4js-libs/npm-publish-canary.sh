#!/bin/bash
cd `dirname $0`
cd `pwd -P`

# Prepare package.json, e.g. dependencies etc. from manifest before publishing
n4js-prepare-npm --globProjects "+(lang|runtime|mangelhaft)/*/manifest.n4mf"  --verbose --npmrcAuthRegistry "${NPM_REGISTRY}" --npmrcAuthToken "${NPM_TOKEN}"

# Publish using canary version
lerna publish --loglevel silly --skip-git --registry="${NPM_REGISTRY}" --force-publish --exact --canary --yes --sort 
