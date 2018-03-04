#!/bin/sh
MYSELF=`which "$0" 2>/dev/null`
[ $? -gt 0 -a -f "$0" ] && MYSELF="./$0"
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" $java_args -jar $MYSELF "$@"
exit 1

# cat stub.sh ./BookIT_client/OSX/BookIT.jar > ./BookIT_client/Linux/BookIT && chmod +x ./BookIT_client/Linux/BookIT