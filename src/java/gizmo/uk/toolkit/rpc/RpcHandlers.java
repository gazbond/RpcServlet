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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>RpcHandlers collects together lists of RpcTargetHandler, RpcArgumentsHandler, RpcReturnValueHandler and
 * RpcExceptionHandler instances specific to the handling of a service object</p>
 *
 * <p>RpcHandlers also stores the services name and class, and a list of filtered method names</p>
 *
 * <p>An RpcHandlers instance is passed to a services static '_configure' method before the service gets
 * registered with the RpcServlet so that the service can register appropriate handlers</p>
 *
 * <p>The same RpcHandlers instance is then used to access these handlers during a remote method
 * invocation</p>
 *
 * @author gareth bond
 */
public class RpcHandlers {

    /**
     * <p>Service name associated with this RpcHandlers instance</p>
     */
    protected String service;

    /**
     * <p>Service class associated with this RpcHandlers instance</p>
     */
    protected Class serviceClass;

    /**
     * <p>List of RpcTargetHandler instances associated with the service</p>
     */
    protected List<RpcTargetHandler> targetHandlers = new ArrayList<RpcTargetHandler>();

    /**
     * <p>List of RpcArgumentsHandler instances associated with the service</p>
     */
    protected List<RpcArgumentsHandler> argumentsHandlers = new ArrayList<RpcArgumentsHandler>();

    /**
     * <p>List of RpcReturnValueHandler instances associated with the service</p>
     */
    protected List<RpcReturnValueHandler> returnValueHandlers = new ArrayList<RpcReturnValueHandler>();

    /**
     * <p>List of RpcExceptionHandler instances associated with the service</p>
     */
    protected List<RpcExceptionHandler> exceptionHandlers = new ArrayList<RpcExceptionHandler>();

    /**
     * <p>List of filtered method names associated with the service</p>
     */
    protected List<String> filterMethods = new ArrayList<String>();

    /**
     * <p>Construct a RpcHandlers instance for a given service name and class</p>
     * 
     * @param serviceName
     * @param serviceClass
     */
    protected RpcHandlers(String serviceName, Class serviceClass) {
        this.service = serviceName;
        this.serviceClass = serviceClass;
    }

    /**
     * <p>Get the list of filtered method names</p>
     */
    protected List<String> getFilterMethods() {
        return filterMethods;
    }

    /**
     * <p>Add a filtered method name</p>
     * 
     * @param method
     */
    public boolean addFilterMethod(String method) {
        return filterMethods.add(method);
    }

    /**
     * <p>Get the list of RpcTargetHandler instances</p>
     */
    protected List<RpcTargetHandler> getTargetHandlers() {
        return targetHandlers;
    }

    /**
     * <p>Get the list of RpcArgumentsHandler instances</p>
     */
    protected List<RpcArgumentsHandler> getArgumentsHandlers() {
        return argumentsHandlers;
    }

    /**
     * <p>Get the list of RpcReturnValueHandler instances</p>
     */
    protected List<RpcReturnValueHandler> getReturnValueHandlers() {
        return returnValueHandlers;
    }

    /**
     * <p>Get the list of RpcExceptionHandler instances</p>
     */
    protected List<RpcExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    /**
     * <p>Add an RpcExceptionHandler instance</p>
     *
     * @param handler
     */
    public boolean addExceptionHandler(RpcExceptionHandler handler) {
        return exceptionHandlers.add(handler);
    }

    /**
     * <p>Add an RpcArgumentsHandler instance</p>
     *
     * @param handler
     */
    public boolean addArgumentsHandler(RpcArgumentsHandler handler) {
        return argumentsHandlers.add(handler);
    }

    /**
     * <p>Add an RpcReturnValueHandler instance</p>
     *
     * @param handler
     */
    public boolean addReturnValueHandler(RpcReturnValueHandler handler) {
        return returnValueHandlers.add(handler);
    }

    /**
     * <p>Add an RpcTargetHandler instance</p>
     *
     * @param handler
     */
    public boolean addTargetHandler(RpcTargetHandler handler) {
        return targetHandlers.add(handler);
    }

    /**
     * <p>Get the service name</p>
     */
    public String getServiceName() {
        return service;
    }

    /**
     * <p>Get the service class</p>
     */
    public Class getServiceClass() {
        return serviceClass;
    }

}
