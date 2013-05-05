/**
 * Copyright (c) 2009, Gareth Bond, http://www.gazbond.co.uk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *     following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *     the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package gizmo.uk.toolkit.rpc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;

/**
 * <p>RpcConfigParser passes a services properties file and configures each service found</p>
 *
 * <p>The properties file specifies the service name as the key and service class as the value</p>
 *
 * <p>The properties file is loaded from the classpath unless its path starts with 'file://' and is therefore
 * loaded from the file system</p>
 *
 * <p>The properties file path can be specified as a ServletConfig param using 'rpc-services-config'</p>
 *
 * <p>If no properties file path is specified a default path of 'rpc-services.properties' is used</p>
 *
 * <p>Each service must specify a static '_config' method that takes an RpcHandlers instance as its only
 * argument</p>
 * 
 * <p>This method is then be used to configure the service by adding appropriate handlers</p>
 *
 * @author gareth bond
 */
public class RpcConfigParser {

    /**
     * <p>ServletConfig param name for services properties file path</p>
     */
    protected final String DEFAULT_PROPS_FILE = "rpc-services.properties";

    /**
     * <p>Default services properties file path</p>
     */
    protected final String CONFIG_ARG_PROPS_FILE = "rpc-services-config";

    /**
     * <p>Pass the services properties file and configures each service found</p>
     * 
     * @param config
     * @param handlers
     */
    public void parseConfig(ServletConfig config,
                            Map<String, RpcHandlers> handlers) {

        //get config argument from ServletConfig
        String servicesPropsFile = config.getInitParameter(CONFIG_ARG_PROPS_FILE);

        //use default if not found
        if(servicesPropsFile == null || (servicesPropsFile != null && servicesPropsFile.length() == 0)) {
            servicesPropsFile = DEFAULT_PROPS_FILE;
        }
        Properties props = new Properties();

        //load the properties file
        try {
            //load via a file reader if path starts with 'file://'
            if(servicesPropsFile.startsWith("file://")) {
                servicesPropsFile = servicesPropsFile.substring(6);
                FileInputStream reader = new FileInputStream(servicesPropsFile);
                props.load(reader);
                reader.close();
            }
            //load from classpath
            else {
                InputStream stream = getClass().getClassLoader().getResourceAsStream(
                        servicesPropsFile);
                props.load(stream);
                stream.close();
            }
        }
        catch(IOException e) {
            throw new RpcError(e);
        }
        //iterate through properties file finding service name and service class
        Iterator<Object> i = props.keySet().iterator();
        while(i.hasNext()) {
            String serviceName = (String) i.next();
            String serviceClassString = props.getProperty(serviceName);
            try {
                //load service class
                Class serviceClass = getClass().getClassLoader().loadClass(
                        serviceClassString);
                Class[] configParams = {RpcHandlers.class};
                Method configMethod = serviceClass.getMethod("_configure", configParams);

                //construct empty RpcHandlers instance for this service
                RpcHandlers rpcHandlers = new RpcHandlers(serviceName, serviceClass);

                //invoke the serice classes static '_config' method
                configMethod.invoke(null, rpcHandlers);

                //add this serice and its RpcHandlers instance to the services map
                handlers.put(serviceName, rpcHandlers);
            }
            catch(Exception e) {
                throw new RpcError(e);
            }
        }
    }

}
