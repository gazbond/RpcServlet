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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>RpcContext stores all relevant information relating to a remote method invocation</p>
 *
 * <p>An RpcContext instance is passed to all handlers registered with a service during a remote method
 * invocation</p>
 *
 * <p>The handler lists available here are unmodifiable<p>
 * 
 * <p>Once a remote method invocation has occured the 'invoked' member will retain a reference to the method
 * that was invoked</p>
 * 
 * @author gareth bond
 */
public class RpcContext {

    /**
     * <p>ServletConfig reference retained</p>
     */
    protected ServletConfig servletConfig;

    /**
     * <p>Service name associated with this RpcContext instance</p>
     */
    protected String service;

    /**
     * <p>Service class associated with this RpcContext instance</p>
     */
    protected Class serviceClass;

    /**
     * <p>Method name associated with this RpcContext instance</p>
     */
    protected String method;

    /**
     * <p>HttpServletRequest associated with this RpcContext instance</p>
     */
    protected HttpServletRequest request;

    /**
     * <p>HttpServletResponse associated with this RpcContext instance</p>
     */
    protected HttpServletResponse response;

    /**
     * <p>List of RpcTargetHandler instances associated with the service</p>
     */
    protected List<RpcTargetHandler> targetHandlers;

    /**
     * <p>List of RpcArgumentsHandler instances associated with the service</p>
     */
    protected List<RpcArgumentsHandler> argumentsHandlers;

    /**
     * <p>List of RpcReturnValueHandler instances associated with the service</p>
     */
    protected List<RpcReturnValueHandler> returnValueHandlers;

    /**
     * <p>List of RpcExceptionHandler instances associated with the service</p>
     */
    protected List<RpcExceptionHandler> exceptionHandlers;

    /**
     * <p>List of filter method names associated with the service</p>
     */
    protected List<String> filterMethods;

    /**
     * <p>Invoked method reference retained</p>
     */
    protected Method invoked;

    /**
     * <p>Contruct a new RpcContext</p>
     * 
     * @param servletConfig
     * @param method
     * @param request
     * @param response
     * @param handlers
     */
    protected RpcContext(ServletConfig servletConfig, String method,
                         HttpServletRequest request, HttpServletResponse response,
                         RpcHandlers handlers) {

        this.servletConfig = servletConfig;
        service = handlers.getServiceName();
        serviceClass = handlers.getServiceClass();
        this.method = method;
        this.request = request;
        this.response = response;
        targetHandlers = Collections.unmodifiableList(handlers.getTargetHandlers());
        argumentsHandlers = Collections.unmodifiableList(handlers.getArgumentsHandlers());
        returnValueHandlers = Collections.unmodifiableList(handlers.getReturnValueHandlers());
        exceptionHandlers = Collections.unmodifiableList(handlers.getExceptionHandlers());
        filterMethods = Collections.unmodifiableList(handlers.getFilterMethods());
    }

    /**
     * <p>Get the HttpServletRequest</p>
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * <p>Get the HttpServletResponse</p>
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * <p>Get the service name</p>
     */
    public String getService() {
        return service;
    }

    /**
     * <p>Get the method name</p>
     */
    public String getMethod() {
        return method;
    }

    /**
     * <p>Get the list of RpcTargetHandler instances</p>
     */
    public List<RpcTargetHandler> getTargetHandlers() {
        return targetHandlers;
    }

    /**
     * <p>Get the list of RpcArgumentsHandler instances</p>
     */
    public List<RpcArgumentsHandler> getArgumentsHandlers() {
        return argumentsHandlers;
    }

    /**
     * <p>Get the list of RpcReturnValueHandler instances</p>
     */
    public List<RpcReturnValueHandler> getReturnValueHandlers() {
        return returnValueHandlers;
    }

    /**
     * <p>Get the list of RpcExceptionHandler instances</p>
     */
    public List<RpcExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    /**
     * <p>Get the list of filtered method names</p>
     */
    public List<String> getFilterMethods() {
        return filterMethods;
    }

    /**
     * <p>Get the service class</p>
     */
    public Class getServiceClass() {
        return serviceClass;
    }

    /**
     * <p>Get the invoked method reference</p>
     */
    public Method getInvoked() {
        return invoked;
    }

    /**
     * <p>Set the invoked method reference</p>
     * 
     * @param invoked
     */
    public void setInvoked(Method invoked) {
        this.invoked = invoked;
    }

}
