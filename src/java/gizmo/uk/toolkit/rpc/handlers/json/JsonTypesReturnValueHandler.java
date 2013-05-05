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
import gizmo.uk.toolkit.rpc.RpcReturnValueHandler;

import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.List;

/**
 * <p>RpcReturnValueHandler implementation that converts various Java types to Javascript types</p>
 *
 * <p>These types are: null, List, Map, boolean, String, long, double and int as well as the corresponding
 * primitive wrappers</p>
 *
 * <p>The returned value is wrapped in a JSON object literal that reiterates information about the invocation</p>
 *
 * <p>Returned JSON object will have a 'return' property if the invocation returns a value</p>
 *
 * <p>Rsponse format: </p>
 * <pre>
 * {
 *  service : [servive-name],
 *  method : [method-name],
 *  timestamp : [yyyyMMddTHH:mm:ss],
 *  return : [object / array / number / string / boolean / null]
 *  }
 * }
 * </pre>
 * 
 * @author gareth bond
 */
public class JsonTypesReturnValueHandler implements RpcReturnValueHandler {

    public boolean handleReturnValue(RpcContext context, Object returnValue)
            throws IOException {

        JSONObject responseObject = new JSONObject();
        try {
            responseObject.put("service", context.getService());
            responseObject.put("method", getMethodDescription(context));
            responseObject.put("timestamp", getTimestamp());
            PrintWriter writer = context.getResponse().getWriter();
            if(returnValue instanceof List || returnValue instanceof Map ||
               returnValue instanceof Boolean || returnValue instanceof String ||
               returnValue instanceof Long || returnValue instanceof Double ||
               returnValue instanceof Integer) {
                responseObject.put("return", returnValue);
                responseObject.write(writer);
                return true;
            }
            else if(returnValue == null) {
                responseObject.put("return", JSONObject.NULL);
                responseObject.write(writer);
                return true;
            }
            else if(returnValue.equals(Void.TYPE)) {
                responseObject.write(writer);
                return true;
            }
        }
        catch(JSONException e) {
            throw new RpcError(e);
        }
        return false;
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
