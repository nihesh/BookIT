------------------------------------------------------------------------------------------------------------

										BOOKIT Server Application

------------------------------------------------------------------------------------------------------------

Setup procedure for linux systems

Note that Server IP refers to the IP of the current system as the current system will be hosting the server
All clients must access the IP address of this server. 

Option - 1

-> While setting up for the first time, the following command needs to be executed

./BookIT.sh  <ServerIP>  <ServerPort> (or) sh BookIT.sh  <ServerIP>  <ServerPort>  

-> Once server IP and port are set-up, the server can be started normally using ./BookIT.sh (or) sh BookIT.sh

Option - 2

./src/AppData/Server/ServerInfo.txt has to be manually configured to mention server IP and port. This is a one time procedure

File format:
<ServerIP>
<ServerPort> 

A sample has been mentioned in the file

Once the setup is done, the jar file can be executed directly using Java 1.8+

--------------------------------------------------------------------------------------------------------------

Resetting the databases to initial state:

Database can be reset using Reset.jar. However, the server must be running in parallel when this jar file is 
executed, else an error is thrown!

Database can also be reset using ./Reset.sh or sh Reset.sh commands on linux. However, the server must be 
running in parallel when this command is executed, else an error is thrown!

Before resetting the server, admin must ensure that all the files in ./src/AppData/StaticTimeTable are filled up properly

--------------------------------------------------------------------------------------------------------------

Developed by
Nihesh Anderson
Harsh Pathak