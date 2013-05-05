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
package gizmo.uk.toolkit.rpc.services;

import gizmo.uk.toolkit.rpc.RpcHandlers;
import gizmo.uk.toolkit.rpc.handlers.SessionScopeTargetHandler;
import gizmo.uk.toolkit.rpc.handlers.json.JsonObjectExceptionHandler;
import gizmo.uk.toolkit.rpc.handlers.json.JsonTypesArgumentsHandler;
import gizmo.uk.toolkit.rpc.handlers.json.JsonTypesReturnValueHandler;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Base service for services that will be held in session scope</p>
 *
 * @author gareth bond
 */
public class BaseJsonSessionScopeService {

    /**
     * <p>Configures the service to use the following handlers: JsonTypesArgumentsHandler,
     * SessionScopeTargetHandler, JsonTypesReturnValueHandler and JsonObjectExceptionHandler</p>
     *
     * <p>Also filters out Java Object methods that should not usually be exposed</p>
     *
     * <p>The filtered methods are: _configure, clone, equals, finalize, hashCode, notify, notifyAll, wait and
     * getClass</p>
     *
     * @param handlers
     */
    public static void _configure(RpcHandlers handlers) {

        handlers.addFilterMethod("_configure");
        handlers.addFilterMethod("clone");
        handlers.addFilterMethod("equals");
        handlers.addFilterMethod("finalize");
        handlers.addFilterMethod("hashCode");
        handlers.addFilterMethod("notify");
        handlers.addFilterMethod("notifyAll");
        handlers.addFilterMethod("wait");
        handlers.addFilterMethod("getClass");
        handlers.addArgumentsHandler(new JsonTypesArgumentsHandler());
        handlers.addTargetHandler(new SessionScopeTargetHandler());
        handlers.addReturnValueHandler(new JsonTypesReturnValueHandler());
        handlers.addExceptionHandler(new JsonObjectExceptionHandler());
    }

    /**
     * <p>Generates a String description of this service objects methods</p>
     *
     * <p>Excludes filtered methods from the description</p>
     *
     * <p>Rsponse format: </p>
     * <pre>
     * {
     *  method : [method-name],
     *  params : [argument-types],
     *  exceptions : [exception-types],
     *  returns : [return-type]
     * {
     * </pre>
     */
    public List _describe() {

        List description = new ArrayList();
        Method[] methods = getClass().getMethods();
        for(int i = 0; i < methods.length; i ++) {
            String methodName = methods[i].getName();
            if( ! methodName.equals("_configure") &&  ! methodName.equals("_describe") &&
                ! methodName.equals("clone") &&  ! methodName.equals("equals") &&  ! methodName.equals(
                    "finalize") &&  ! methodName.equals("hashCode") &&  ! methodName.equals(
                    "notify") &&  ! methodName.equals("notifyAll") &&  ! methodName.equals("wait") &&
                ! methodName.equals("getClass")) {
                Class returnType = methods[i].getReturnType();
                Class[] paramTypes = methods[i].getParameterTypes();
                List paramsList = new ArrayList();
                for(int x = 0; x < paramTypes.length; x ++) {
                    paramsList.add(paramTypes[x].getName());
                }
                Class[] exceptionTypes = methods[i].getExceptionTypes();
                List exceptionsList = new ArrayList();
                for(int x = 0; x < exceptionTypes.length; x ++) {
                    exceptionsList.add(exceptionTypes[x].getName());
                }
                Map current = new HashMap();
                current.put("method", methodName);
                current.put("params", paramsList);
                current.put("exceptions", exceptionsList);
                current.put("returns", returnType.getName());
                description.add(current);
            }
        }
        return description;
    }

}
