#!/bin/sh

if [ "$#" -ne 0 ]; then
echo $1"\n"$2 > ServerInfo.txt
fi 
java -jar BookIT.jar