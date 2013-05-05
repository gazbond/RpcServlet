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

import gizmo.uk.toolkit.rpc.RpcArgumentsHandler;
import gizmo.uk.toolkit.rpc.RpcContext;
import gizmo.uk.toolkit.rpc.RpcError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>RpcArgumentsHandler implemenation that converts a JSON array literal into a List of various Java types</p>
 *
 * <p>These types are: null, List, Map, boolean, String, long, double and int as well as the corresponding 
 * primitive wrappers</p>
 *
 * @author gareth bond
 */
public class JsonTypesArgumentsHandler implements RpcArgumentsHandler {

    /**
     * <p>HTTP request parameter the arguments are read from</p>
     */
    protected final String URL_PARAM_JSON_ARRAY = "a";

    public List<Object> getArguments(RpcContext context) {

        List<Object> arguments = new ArrayList<Object>();
        String arrayString = context.getRequest().getParameter(
                URL_PARAM_JSON_ARRAY);
        JSONArray array = null;

        if(arrayString != null && arrayString.length() != 0) {
            try {
                array = new JSONArray(arrayString);
                for(int i = 0; i < array.length(); i ++) {
                    arguments.add(convert(array.get(i)));
                }
            }
            catch(Exception e) {
                throw new RpcError(e);
            }
        }
        return arguments;
    }

    /**
     * <p>Recursively convert JSON types into corresponding Java types</p>
     * 
     * @param obj
     * @throws JSONException
     */
    protected Object convert(Object obj) throws JSONException {

        Object returnObj = null;
        if(obj instanceof JSONArray) {
            JSONArray objArray = (JSONArray) obj;
            List<Object> objList = new ArrayList();
            for(int i = 0; i < objArray.length(); i ++) {
                Object value = objArray.get(i);
                objList.add(value);
            }
            returnObj = objList;
        }
        else if(obj instanceof JSONObject) {
            JSONObject objObject = (JSONObject) obj;
            Map<String, Object> objMap = new HashMap<String, Object>();
            Iterator<String> i = objObject.keys();
            while(i.hasNext()) {
                String key = i.next();
                Object value = convert(objObject.get(key));
                objMap.put(key, value);
            }
            returnObj = objMap;
        }
        else if(obj instanceof Integer || obj instanceof Boolean || obj instanceof String ||
                obj instanceof Long || obj instanceof Double) {
            returnObj = obj;
        }
        return returnObj;
    }

}
