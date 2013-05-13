### Installing RCP-Servlet:

RCP-Servlet was created with Java SE 6 so it is probably best to run it with Tomcat 6.

Download Tomcat 6 and extract.

Clone RpcServlet from GitHub using the following:
	
	$ git clone git@github.com:gazbond/RpcServlet.git

Change directory to [rpc-servlet-directory] and run the ant build file using the following:
	
	$ ant

You should then see:
	
	RpcServlet
	    |-web
	        |-WEB-INF
	            |-classes
	                |-rpc-services.properties
	                |-gizmo
	                |-org

Create the file rpc-servlet.xml in [tomcat-directory]/conf/Catalina/localhost/ with the following contents:
	
	<?xml version="1.0" encoding="UTF-8"?>
	<Context docBase="[rpc-servlet-directory]/web" path="/rpc-servlet"/>

Start Tomcat and go to http://localhost:8080/rpc-servlet/ in your browser.