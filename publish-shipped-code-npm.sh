echo "Start publishing shipped code to npm registry"
cd `dirname $0`
cd `pwd -P`

# At this point, we are in the root folder of n4js repo, i.e. n4js

# Install n4js-prepare-npm npm
npm install n4js-prepare-npm

# Navigate to shipped-code folder
cd plugins/org.eclipse.n4js.external.libraries/shipped-code

# Testing purpose: publish to test NEXUS npm registry
NPM_REGISTRY="http://webclients3-nexus.service.cd-dev.consul/repository/npm-internal/"


# Use n4js-npm-prepare to prepare package.json (e.g. dependencies, licenses) of npms to be published
n4js-prepare-npm -globProjects **/manifest.n4mf \
	--verbose	\
	--npmrcAuthRegistry "${NPM_REGISTRY}" \
	--npmrcAuthToken "${NPM_TOKEN}"

# Publish all projects in shipped-code folder to npm registry
#	--skip-git			Don't run any git commands
#	--registry			The npm registry to publish to
#	--force-publish=*	For publish for all packages (skips git diff check for changed packages)	
#	--exact				Specify updated dependencis in updated packages exactly
#	--canary			Creates canary version, e.g. 1.0.0 -> 1.1.0-alpha.e1sa2334
# 	--yes				Skip all confirmation promps
lerna publish \
	--loglevel silly \	
	--skip-git \ 
	--registry="${NPM_REGISTRY}"
	--force-publish=*
	--exact
	--canary
	--yes
echo "End of publishing shipped code to npm registry"
