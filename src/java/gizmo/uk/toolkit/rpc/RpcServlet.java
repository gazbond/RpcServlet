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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * <p>RpcServlet is an HttpServlet sub class that allows Java objects to be registered as web services
 * so that their methods can be remotely called over HTTP</p>
 *
 * <p>RpcServlet expects URL's to formatted as follows: [servlet-mapping]/[service-name]/[method-name]/</p>
 *
 * <p>RpcServlet farms out various aspects of how a service method is invoked to a series of handler objects
 * that operate in a chain of responsibility pattern</p>
 *
 * <p>These handlers handle how method arguments are interpreted from the HTTP request, how the service object
 * or target is accessed and how a return value or exception resulting from a method invocation is passed back
 * with the HTTP response</p>
 *
 * <p>An RpcConfigParser instance is used to configure service objects and their handlers so that a map of
 * service names corresponding to RpcHandlers instances can be maintained and used to look up how a specific
 * service should be handled</p>
 *
 * <p>An RpcContext instance containing relevant information about a request is assembled for each method
 * invocation and is passed to each of its service handlers</p>
 *
 * @author gareth bond
 */
public class RpcServlet extends HttpServlet {

    /**
     * <p>Map of service names corresponding to RpcHandlers instances used to look up services</p>
     */
    protected Map<String, RpcHandlers> handlers = new HashMap<String, RpcHandlers>();

    /**
     * <p>ServletConfig object reference maintained</p>
     */
    protected ServletConfig config;

    /**
     * <p>Configure the Servlet</p>
     *
     * <p>Configuration is handled by an RpcConfigParser instance</p>
     * 
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        this.config = config;
        RpcConfigParser configParser = new RpcConfigParser();
        configParser.parseConfig(config, handlers);
    }

    /**
     * <p>Handle HTTP POST request</p>
     *
     * <p>POST and GET are handled the same therefore this method passes its arguments straight to
     * doGet(HttpServletRequest, HttpServletResponse)</p>
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }

    /**
     * <p>Handle HTTP GET and POST request</p>
     *
     * <p>This method is the main routine for a remote method invocation:</p>
     *
     * <ul>
     * <li>Retrieve the service name and method name from the HTTP request</li>
     * <li>Look up the service and its handlers</li>
     * <li>Assemble an RpcContext for the request</li>
     * <li>Retrieve method arguments from the HTTP request via the appropriate handler</li>
     * <li>Retrieve the service object or target via the appropriate handler</li>
     * <li>Invoke the requested method on the service object or target</li>
     * <li>Write any return value or exception to the HTTP response via the appropriate handler</li>
     * </ul>
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            //get service path - [serice-name]/[method-name]/
            String path = req.getPathInfo();
            if(path != null && path.startsWith("/")) {
                path = path.substring(1);
            }
            int index = -1;
            if(path != null) {
                index = path.indexOf("/");
            }

            //no service path? list services instead
            if(path == null || (path != null && path.length() == 0)) {
                listServices(resp);
                return;
            }

            //invalid service path
            if(index == -1 || index >= path.length() - 1) {
                throw new RpcError(RpcMessages.getMessage("invalidUrl", path));
            }

            //get service name and method from service path
            String service = path.substring(0, index);
            String methodName = path.substring(index + 1);

            //invalid method name
            if(methodName.indexOf("/") != -1 || service.length() == 0 || methodName.length() == 0) {
                throw new RpcError(RpcMessages.getMessage("invalidUrl", path));
            }

            //get service handlers for this service
            RpcHandlers handler = handlers.get(service);

            //service not found
            if(handler == null) {
                throw new RpcError(RpcMessages.getMessage("unknownService", service));
            }

            //service method has been filtered
            if( ! canInvokeMethod(methodName, handler.getFilterMethods())) {
                throw new RpcError(RpcMessages.getMessage("methodFiltered", methodName));
            }

            //contruct the context for this service call
            RpcContext context = new RpcContext(config, methodName, req, resp, handler);

            synchronized(handler) {
                //get the arguments
                List<Object> arguments = getArguments(context, handler.getArgumentsHandlers());

                //get the target
                Object target = getTarget(context, handler.getTargetHandlers());
                try {
                    //invoke the target
                    Object returnValue = invoke(context, target, arguments);

                    //handle any return value
                    handleReturnValue(context, returnValue, handler.getReturnValueHandlers());
                }
                catch(RpcError e) {

                    //this kind of error should not be passed to exception handlers
                    throw e;
                }
                catch(Throwable exception) {

                    //handle any exception
                    handleException(context, exception, handler.getExceptionHandlers());
                }
            }
        }
        catch(Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * <p>List all services registered with the Servlet in JSON format</p>
     *
     * @param resp
     * @throws IOException
     * @throws JSONException
     */
    protected void listServices(HttpServletResponse resp) throws IOException, JSONException {

        //TODO: this should be extensible and not fixed to JSON format
        JSONArray array = new JSONArray();
        Iterator<String> i = handlers.keySet().iterator();
        while(i.hasNext()) {
            String name = i.next();
            array.put(name);
        }
        PrintWriter writer = resp.getWriter();
        array.write(writer);
    }

    /**
     * <p>Retrieve method arguments from the HTTP request via RpcArgumentsHandler instances</p>
     *
     * <p>If this method returns an empty List then there are no arguments for this request</p>
     *
     * <p>If this method returns null then there is no appropriate RpcArgumentsHandler instnace for this
     * request and therefore it cannot be handled</p>
     * 
     * @param context
     * @param argumenntsHandlers
     */
    protected List<Object> getArguments(RpcContext context,
                                        List<RpcArgumentsHandler> argumenntsHandlers) {

        Iterator<RpcArgumentsHandler> i = argumenntsHandlers.iterator();
        while(i.hasNext()) {
            RpcArgumentsHandler handler = i.next();
            List<Object> params = handler.getArguments(context);
            if(params != null) {
                return params;
            }
        }
        return null;
    }

    /**
     * <p>Retrieve the service object via RpcTargetHandler instances</p>
     *
     * <p>If this method returns null then there is no appropriate RpcTargetHandler instance for this
     * request and therefore it cannot be handled</p>
     * 
     * @param context
     * @param targetHandlers
     */
    protected Object getTarget(RpcContext context,
                               List<RpcTargetHandler> targetHandlers) {

        Iterator<RpcTargetHandler> i = targetHandlers.iterator();
        while(i.hasNext()) {
            RpcTargetHandler handler = i.next();
            Object target = handler.getTarget(context);
            if(target != null) {
                return target;
            }
        }
        return null;
    }

    /**
     * <p>Handle writing any return value from a method invocation to the HTTP response via 
     * RpcReturnValueHandler instances</p>
     *
     * <p>Throws an RpcError if no RpcReturnValueHandler handles the return value</p>
     *
     * @param context
     * @param returnValue
     * @param returnParamHandlers
     * @throws IOException
     */
    protected void handleReturnValue(RpcContext context, Object returnValue,
                                     List<RpcReturnValueHandler> returnParamHandlers) throws
            IOException {

        Iterator<RpcReturnValueHandler> i = returnParamHandlers.iterator();
        while(i.hasNext()) {
            RpcReturnValueHandler handler = i.next();
            if(handler.handleReturnValue(context, returnValue)) {
                return;
            }
        }
        throw new RpcError(RpcMessages.getMessage("invalidReturnType", returnValue.getClass().
                getName()));
    }

    /**
     * <p>Handle writing any exception from a method invocation to the HTTP response via
     * RpcExceptionHandler instances</p>
     *
     * <p>Throws an RpcError if no RpcExceptionHandler handles the exception</p>
     * 
     * @param context
     * @param exception
     * @param exceptionHandlers
     * @throws IOException
     */
    protected void handleException(RpcContext context, Throwable exception,
                                   List<RpcExceptionHandler> exceptionHandlers) throws IOException {

        Iterator<RpcExceptionHandler> i = exceptionHandlers.iterator();
        while(i.hasNext()) {
            RpcExceptionHandler handler = i.next();
            if(handler.handleException(context, exception)) {
                return;
            }
        }
        throw new RpcError(RpcMessages.getMessage("invalidExceptionType", exception.getClass().
                getName()));
    }

    /**
     * <p>Invoke the requested method on the target service obejct</p>
     *
     * <p>This method searches the target object for the requested method name that matches the supplied
     * arguments list and if found invokes it</p>
     *
     * <p>Throws an RpcError if no matching method can be found</p>
     * 
     * <p>Returns the return value from the method invocation or Void.TYPE if the method does not return
     * a value</p>
     * 
     * @param context
     * @param target
     * @param arguments
     * @throws Throwable
     */
    protected Object invoke(RpcContext context, Object target, List<Object> arguments) throws Throwable {

        //convert arguments list to array of Class types
        Class[] argClasses = null;
        Object[] argValues = null;
        if(arguments != null) {
            argValues = arguments.toArray();
            argClasses = new Class[argValues.length];

            for(int i = 0; i < argValues.length;  ++ i) {
                argClasses[i] = argValues[i].getClass();
            }
        }
        //search for a matching method on the target
        Method[] methods = target.getClass().getMethods();
        outer:
        for(int i = 0; i < methods.length;  ++ i) {
            Method method = methods[i];
            if(method.getName().equals(context.getMethod())) {
                Class[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length == argClasses.length) {
                    for(int j = 0; j < parameterTypes.length;  ++ j) {
                        Class type = parameterTypes[j];
                        
                        //is the parameter primitive and of a compatible type
                        if(type.isPrimitive()) {
                            if(type == Double.TYPE && argClasses[j] != Double.class) {
                                continue outer;
                            }
                            if(type == Integer.TYPE && argClasses[j] != Integer.class) {
                                continue outer;
                            }
                            if(type == Boolean.TYPE && argClasses[j] != Boolean.class) {
                                continue outer;
                            }
                            if(type == Long.TYPE && argClasses[j] != Long.class) {
                                continue outer;
                            }
                        }
                        else {
                            if( ! parameterTypes[j].isAssignableFrom(argClasses[j])) {
                                continue outer;
                            }
                        }
                    }
                    //invoke a matching method
                    try {
                        context.setInvoked(method);
                        Object returnValue = method.invoke(target, argValues);
                        if(method.getReturnType().equals(Void.TYPE)) {
                            return Void.TYPE;
                        }
                        else {
                            return returnValue;
                        }
                    }
                    catch(IllegalAccessException e) {
                        throw new RpcError(e);
                    }
                    catch(InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }
        }
        //build error message
        StringBuilder error = new StringBuilder();
        error.append(RpcMessages.getMessage("methodDoesntExist"));
        error.append(' ');
        error.append(context.getMethod());
        error.append('(');
        for(int i = 0; i < argClasses.length;  ++ i) {
            error.append(argClasses[i].getName());
            if(i < argClasses.length - 1) {
                error.append(", ");
            }
        }
        error.append(')');
        throw new RpcError(error.toString());
    }

    /**
     * <p>Can the method be invoked i.e. does it appear in the filter methods list</p>
     *
     * @param methodName
     * @param filterMethods
     */
    protected boolean canInvokeMethod(String methodName,
                                      List<String> filterMethods) {

        return  ! filterMethods.contains(methodName);
    }

}
