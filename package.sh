#!/bin/bash
THIS_DIR="$(basename $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd ))"
TARBALLNAME=${THIS_DIR}.zip
mvn -q clean
rm -f output.txt nohup.out
zip -r -X ../${TARBALLNAME} pom.xml README.md LICENSE src package.sh

