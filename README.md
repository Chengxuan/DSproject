*************SHELL FOR UNIX*****************************

ds.sh:

[FUNCTION]

Terminal:


//first cd to my folder

$ sudo su

\# sh ds.sh /apache-tomcat-7.0.32/<———— this should be replaced by your tomcat home


Then:

go to “http://localhost:8080/ds/“ in your Browser



*******************************************************************

|————————————|

|************|

|***Warning**|<——————————————— I am Important, If you are using Safari Browser

|************|

|————————————|


if your are try to use file compress and download the .gz file using safari

please go to Safari——>Preferences——>General——>Uncheck “Open “safe” files after downloading. Other wise it will

automatically decompress the file.


*******************************************************************

Main Features:


ds.jar or crypto.jar: they are same thing

[DESCRIPTION]

jar file for start RMI server

[BASIC]

java -cp ./ds.jar ie.gmit.AsyncService

————>start an RMI server

port: 1099

registry name: ChengEC

[EXTRA]

java -cp ./ds.jar ie.gmie.AsyncService [-n RegistryName][-p PortNumber][-h Help]

————>start an RMI server with customise port number and service name, you should also modify the web.xml after doing this.


ds.war:

[DESCRIPTION]

war file for Tomcat 

[BASIC]

deploy web service can do encrypt, decrypt, compress and decompress string.

Use thread check Inqueue, processing and put into out Queue

[EXTRA]

Can also process file, and you can process both together as well

You can download processed file from URL


src:

[DESCRIPTION]

source code

[COMPILE]

<!—You should change TOMCAT_HOME to your Tomcat path——!>

export TOMCAT_HOME=/apache-tomcat-7.0.32

export CLASSPATH=.:$TOMCAT_HOME/lib/annotations-api.jar:$TOMCAT_HOME/lib/catalina-ant.jar:$TOMCAT_HOME/lib/catalina-ha.jar:$TOMCAT_HOME/lib/catalina-tribes.jar:$TOMCAT_HOME/lib/catalina.jar:$TOMCAT_HOME/lib/ecj-4.2.2.jar:$TOMCAT_HOME/lib/el-api.jar:$TOMCAT_HOME/lib/jasper-el.jar:$TOMCAT_HOME/lib/jasper.jar:$TOMCAT_HOME/lib/jsp-api.jar:$TOMCAT_HOME/lib/servlet-api.jar:$TOMCAT_HOME/lib/tomcat-api.jar:$TOMCAT_HOME/lib/tomcat-coyote.jar:$TOMCAT_HOME/lib/tomcat-dbcp.jar:$TOMCAT_HOME/lib/tomcat-i18n-es.jar:$TOMCAT_HOME/lib/tomcat-i18n-fr.jar:$TOMCAT_HOME/lib/tomcat-i18n-ja.jar:$TOMCAT_HOME/lib/tomcat-jdbc.jar:$TOMCAT_HOME/lib/tomcat-util.jar:$TOMCAT_HOME/tomcat7-websocket.jar:$TOMCAT_HOME/lib/websocket-api.jar



*******************************************************************


How to USE:

1.copy ds.war to your tomcat webapps folder

cp ./ds.war $TOMCAT_HOME/webapps/

2.start RMI Service:

java -cp ./ds.jar ie.gmit.AsyncService

3.start or restart tomcat 

sudo $TOMCAT_HOME/bin/shutdown.sh

sudo $TOMCAT_HOME/bin/startup.sh

4.go to "http://localhost:8080/ds/"

start using



********************************************************************


Borrow Code(all can be found in source Code comments)

1.file compress and decompress byte buffer

From Ru Peng. 

// read and write from inputfile to outputfile through a buffer bytes

// this buff array could be improved by using PureBAOS class, but I

// haven't fully

// understand the GZIP mechanism

// ***********************//

// get help form Ru Peng 

// I was used PureBAOS to create dynamic buffer before,but not work


// **********************//


2.generate key from specific string

From Adeel Gilani. 

// ******************//

// get help from Adeel Gilani 

// I was using a String to Key method before, it's not work on Unix

// system

// I post the problem on Moodle

// *****************//

// --------The old method---------//

// |KeyGenerator kgen = KeyGenerator.getInstance("AES");

// |kgen.init(128, new SecureRandom(key.getBytes()));

// |SecretKey secretKey = kgen.generateKey();

// |byte[] enCodeFormat = secretKey.getEncoded();

// |tempKey = new SecretKeySpec(enCodeFormat, "AES");

// --------The old method---------//

