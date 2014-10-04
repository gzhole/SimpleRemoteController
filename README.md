How to setup development environment:

1. Download Scala IDE (Eclipse)
http://scala-ide.org/download/sdk.html

2. Download SBT
http://www.scala-sbt.org/

3. Install Git Repository eclipse Plug-in
http://www.eclipse.org/egit/download/

4. In Eclipse Git Repositories view, connect to Github with following URL (right click mouse-> paste Repository URL 
https://github.com/gzhole/SimpleRemoteController.git)

5. After finishing loading source codes, goto source directory build jar file by running: cd SimpleRemoteController,
sbt,compile,test,assembly

6. Copy jar file to vm, it's in 
SimpleRemoteController\target\scala-2.10\AcmeAirAgent.jar

7. Start agent with 
java -jar AcmeAirAgent.jar or if you have multiple IPs, java -jar AcmeAirAgent.jar localIPAddress

8. On the client machine, you can:

a. copy local file to remote vm, e.g. 
java -cp AcmeAirAgent.jar Client copy a.txt  192.168.0.111

b. execute remote command, e.g.
java -cp AcmeAirAgent.jar Client "cat /tmp/a.txt"  192.168.0.111

c. execute local command (if agent is started locally), e.g.
java -cp AcmeAirAgent.jar Client hostname