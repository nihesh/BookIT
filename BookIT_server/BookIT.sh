#!/bin/sh

if [ "$#" -ne 0 ]; then
echo $1"\n"$2 > ./src/AppData/Server/ServerInfo.txt
fi 
java -jar Server.jar