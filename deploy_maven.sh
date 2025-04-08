#!/bin/bash

EXCLUDES_MODULES=("bourbon" "gin" "scotch")

ALL_MODULES=$(find . -name pom.xml | sed 's|^\./||' | sed 's|/pom.xml$||')

INCLUDED_MODULES=()
for module in $ALL_MODULES; do
  skip=false
  for ex in "${EXCLUDES_MODULES[@]}"; do
    if [[ $module == $ex* ]]; then
      skip=true
      break
    fi
  done
  if [ "$skip" = false ]; then
    INCLUDED_MODULES+=("$module")
  fi
done

PL_VALUE=$(IFS=, ; echo "${INCLUDED_MODULES[*]}")

mvn -B clean deploy -pl $PL_VALUE -Dmaven.test.skip=true