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
package gizmo.uk.toolkit.rpc.handlers.json;

import gizmo.uk.toolkit.rpc.RpcContext;
import gizmo.uk.toolkit.rpc.RpcError;
import gizmo.uk.toolkit.rpc.RpcExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>RpcExceptionHandler implementation that convets an Thowable type into a JSON object literal</p>
 *
 * <p>The exception is wrapped in a JSON object literal that reiterates information about the invocation</p>
 *
 * <p>Returned JSON object will either have a 'error' or 'exception' property indicating the type of
 * exception that was thrown</p>
 * 
 * <p>Rsponse format: </p>
 * <pre>
 * {
 *  service : [servive-name],
 *  method : [method-name],
 *  timestamp : [yyyyMMddTHH:mm:s],
 *  error / exception : {
 *      class : [Java-Class],
 *      message : [String],
 *      cause : {
 *          class : [Java-Clas],
 *          message : [String],
 *          cause : {
 *              [etc.]
 *          }
 *      }
 *  }
 * }
 * </pre>
 * 
 * @author gareth bond
 */
public class JsonObjectExceptionHandler implements RpcExceptionHandler {

    public boolean handleException(RpcContext context, Throwable exception)
            throws IOException {

        JSONObject responseObject = new JSONObject();
        JSONObject exceptionObject = null;
        try {
            responseObject.put("service", context.getService());
            responseObject.put("method", getMethodDescription(context));
            responseObject.put("timestamp", getTimestamp());
            exceptionObject = getExceptionObject(exception);
            if(exception instanceof Error) {
                responseObject.put("error", exceptionObject);
            }
            else {
                responseObject.put("exception", exceptionObject);
            }
            PrintWriter printWriter = context.getResponse().getWriter();
            responseObject.write(printWriter);
            return true;
        }
        catch(JSONException e) {
            throw new RpcError(e);
        }
    }

    /**
     * <p>Get a String description of the invoked method</p>
     * 
     * @param context
     */
    protected String getMethodDescription(RpcContext context) {
        String description = context.getInvoked().toGenericString();
        int index = description.indexOf(context.getMethod());
        description = description.substring(index);
        return description;
    }

    /**
     * <p>Recursively construct a JSONObject from the exception and any cause exceptions</p>
     * 
     * @param exception
     * @throws JSONException
     */
    protected JSONObject getExceptionObject(Throwable exception)
            throws JSONException {

        JSONObject exceptionObject = new JSONObject();
        exceptionObject.put("class", exception.getClass().getName());
        String message = exception.getMessage();
        if(message == null) {
            message = "";
        }
        exceptionObject.put("message", message);

        Throwable cause = exception.getCause();
        if(cause != null) {
            JSONObject object = getExceptionObject(cause);
            exceptionObject.put("cause", object);
        }
        return exceptionObject;
    }

    /**
     * <p>Generate a timestamp String in the format: yyyyMMddTHH:mm:ss
     */
    protected String getTimestamp() {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
        try {
            return formatter.format(now);
        }
        catch(Exception e) {
            throw new RpcError(e);
        }
    }

}
