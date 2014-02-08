export TOMCAT_HOME=/$1
sudo cp ./ds.war $TOMCAT_HOME/webapps/
sudo $TOMCAT_HOME/bin/shutdown.sh
sudo $TOMCAT_HOME/bin/startup.sh
java -cp ./ds.jar ie.gmit.AsyncService
