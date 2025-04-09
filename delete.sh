set -x
parse() {
    local pom=$1
    groupId=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="groupId"]/text()' "$pom" 2>/dev/null)
    if [ -z "$groupId" ]; then
        groupId=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="parent"]/*[local-name()="groupId"]/text()' "$pom" 2>/dev/null)
    fi
    artifactId=$(xmllint --xpath '/*[local-name()="project"]/*[local-name()="artifactId"]/text()' "$pom" 2>/dev/null)
    if [ -n "$groupId" ] && [ -n "$artifactId" ]; then
        curl -s -L -X DELETE -H "Authorization: Bearer $PERSONAL_ACCESS_TOKEN" https://api.github.com/user/packages/maven/$groupId.$artifactId
    fi
}
excluded_modules=("bourbon" "gin" "scotch")
find . -name "pom.xml" | while read pom; do
    pom_dir=$(dirname "${pom#./}")
    if [[ ! " ${excluded_modules[@]} " =~ " $pom_dir " ]]; then
        parse $pom
    fi
done
excluded_projects=$(IFS=,; echo "${excluded_modules[*]/#/!}")
echo "excluded_projects=$excluded_projects" >> $GITHUB_ENV