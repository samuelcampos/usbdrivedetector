#!/usr/bin/env bash

echo "Check if should deploy..."

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "Deploying to Maven Central"

    #MVN_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)

    mvn deploy -DskipTests=true -P release --settings ./cd/mvnsettings.xml
fi