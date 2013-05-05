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

import gizmo.uk.toolkit.rpc.services.BaseJsonSessionScopeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>TestService is a JSON-RPC service to testing remote procedural calls between a
 * Javascript client and a web Java service</p>
 * 
 * @author gareth bond
 */
public class TestService extends BaseJsonSessionScopeService {

    /**
     * <p>Test int / Integer</p>
     * 
     * @param i int / Integer to test
     * @return the supplied input param i
     */
    public int echo(int i) {

        print("public int echo(int = " + i + ")");
        return i;
    }

    /**
     * <p>Test double / Double</p>
     * 
     * @param d double / Double to test
     * @return the supplied input param d
     */
    public double echo(double d) {

        print("public double echo(double = " + d + ")");
        return d;
    }

    /**
     * <p>Test long / Long</p>
     * 
     * @param l long / Long to test
     * @return the supplied input param l
     */
    public long echo(long l) {

        print("public long echo(long = " + l + ")");
        return l;
    }

    /**
     * <p>Test boolean / Boolean</p>
     *
     * @param b boolean / Boolean to test
     * @return the supplied input param b
     */
    public boolean echo(boolean b) {

        print("public boolean echo(boolean = " + b + ")");
        return b;
    }

    /**
     * <p>Test String</p>
     *
     * @param s String to test
     * @return the supplied input param s
     */
    public String echo(String s) {

        print("public String echo(String = " + s + ")");
        return s;
    }

    /**
     * <p>Test Map</p>
     *
     * @param m Map to test
     * @return the supplied input param m
     */
    public Map echo(Map m) {

        print("public Map echo(Map = " + m + ")");
        return m;
    }

    /**
     * <p>Test List</p>
     *
     * @param l List to test
     * @return the supplied input param l
     */
    public List echo(List l) {

        print("public List echo(List = " + l + ")");
        return l;
    }

    /**
     * <p>Test all primitives</p>
     * 
     * @param i int / Integer to test
     * @param d double / Double to test
     * @param l long / Long to test
     * @param b boolean / Boolean to test
     * @param s String to test
     * @return the supplied input params as a List
     */
    public List echo(int i, double d, long l, boolean b, String s) {

        print("public List echo(int = " + i + ", double = " + d + ", long = " + l +
              ", boolean = " + b + ", String = " + s + ")");
        List list = new ArrayList();
        list.add(i);
        list.add(d);
        list.add(l);
        list.add(b);
        list.add(s);
        return list;
    }

    /**
     * <p>Test Integer<p>
     *
     * @param i Integer to test
     * @return the supplied input param i
     */
    public Integer echoWrappr(Integer i) {

        print("public Integer echoWrappr(Integer = " + i + ")");
        return i;
    }

    /**
     * <p>Test Double</p>
     *
     * @param d Double to test
     * @return the supplied input param d
     */
    public Double echoWrappr(Double d) {

        print("public Double echoWrappr(Double = " + d + ")");
        return d;
    }

    /**
     * <p>Test Long<p>
     *
     * @param l Long to test
     * @return the supplied input param l
     */
    public Long echoWrappr(Long l) {

        print("public Long echoWrappr(Long = " + l + ")");
        return l;
    }

    /**
     * <p>Test Boolean</p>
     *
     * @param b Boolean to test
     * @return the supplied input param b
     */
    public Boolean echoWrappr(Boolean b) {

        print("public Boolean echoWrappr(Boolean = " + b + ")");
        return b;
    }

    /**
     * <p>Test void</p>
     */
    public void returnVoid() {
    }

    /**
     * <p>Test null</p>
     *
     * @return null
     */
    public String returnNull() {

        return null;
    }

    /**
     * <p>Test throwing an Exception</p>
     *
     * @throws Exception Exception tested
     */
    public void throwException() throws Exception {

        print("public void throwException() throws Exception");
        Error e1 = new Error("error cause");
        Exception e2 = new Exception("exception cause", e1);
        throw new Exception("exception!", e2);
    }

    /**
     * <p>Test throwing an Error</p>
     */
    public void throwError() {

        print("public void throwError()");
        throw new Error("error!");
    }

    /**
     * <p>Session scoped variable</p>
     */
    private String savedValue = null;

    /**
     * <p>Test saving to session scope</p>
     *
     * @param value value to be saved to session scope
     */
    public void saveValue(String value) {

        print("public void saveValue(String value = " + value + ")");
        savedValue = value;
    }

    /**
     * <p>Test retrieving from session scope</p>
     *
     * @return value that was saved to session scope or null if no value has been saved yet
     */
    public String retrieveValue() {

        print("public String retrieveValue()");
        return savedValue;
    }

    /**
     * <p>Test whether a value has been saved to session scope</p>
     * 
     * @return whther a value has been saved to session scope
     */
    public boolean hasValue() {

        print("public boolean hasValue()");
        if(savedValue == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * <p>Test deleting a value from session scope</p>
     */
    public void deleteValue() {

        print("public void deleteValue()");
        savedValue = null;
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
