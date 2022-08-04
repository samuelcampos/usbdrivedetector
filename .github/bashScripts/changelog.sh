#!/bin/sh
#Simple bash script to generate CHANGELOG.md

git fetch --tags
LASTTAG=$(git tag -l --sort=v:refname | tail -1)
COMMITLIST=$(git log --no-merges --format=%s ${LASTTAG}..HEAD)
PREV_CHANGELOG=$(cat ./CHANGELOG.md)
RELEASEVERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed 's/-SNAPSHOT//')

echo -e "## Version $RELEASEVERSION\n"  > ./CHANGELOG.md
IFS=$'\n'; COMMIT_ARRAY=($COMMITLIST); unset IFS;
for i in "${COMMIT_ARRAY[@]}"
do
  echo "- $i"  >> ./CHANGELOG.md
done


echo -e "\n\n" >> ./CHANGELOG.md
echo "$PREV_CHANGELOG" >> ./CHANGELOG.md