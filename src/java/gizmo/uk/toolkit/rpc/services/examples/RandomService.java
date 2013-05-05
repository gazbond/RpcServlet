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
package gizmo.uk.toolkit.rpc.services.examples;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import gizmo.uk.toolkit.rpc.services.BaseJsonSessionScopeService;

/**
 * <p>RandomService is a simple JSON-RPC service for generating random Strings</p>
 * <p>It is a sub class of BaseJsonSessionScopeService and therefore stores a last
 * generated random String in session scope</p>
 * 
 * @author gareth bond
 */
public class RandomService extends BaseJsonSessionScopeService {

    /**
     * <p>Char lookup for random String generation</p>
     */
    private final String CHAR_LOOKUP = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    /**
     * <p>The last generated random String</p>
     */
    private String lastRandomString;

    /**
     * <p>Generate a random String of a certain length</p>
     * 
     * @param length the length of the String to be generated
     * @return a random String
     */
    public Map createRandomString(int length) {

        print("public Map createRandomString(int = " + length + ")");
        Random random = new Random(System.currentTimeMillis());
        StringBuilder randomString = new StringBuilder();
        for(int i = 0; i < length; i ++) {
            int index = random.nextInt(CHAR_LOOKUP.length());
            randomString.append(CHAR_LOOKUP.charAt(index));
        }
        lastRandomString = randomString.toString();
        Map returnObject = new HashMap();
        returnObject.put("created", lastRandomString);
        returnObject.put("length", length);
        return returnObject;
    }

    /**
     * <p>Get the last generated random String that has been stored in seesion scope</p>
     *
     * @return a last generated random String or null if no String has been generated yet
     */
    public String getLastRandomString() {

        print("public Map getLastRandomString()");
        return lastRandomString;
    }

    /**
     * <p>Print an Object to System.out</p>
     * 
     * @param obj Object to print
     */
    private void print(Object obj) {

        System.out.println(obj);
    }

}
