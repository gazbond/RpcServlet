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

import gizmo.uk.toolkit.rpc.services.BaseJsonApplicationScopeService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ChatService is a JSON-RPC service for handling communications between web based clients</p>
 * <p>It is a sub class of BaseJsonApplicationScopeService and therefore connects client messages
 * via application scoped Maps</p>
 * 
 * @author gareth bond
 */
public class ChatService extends BaseJsonApplicationScopeService {

    /**
     * <p>Map of logged in users and their posts</p>
     */
    Map<String, LinkedList<String>> users = new HashMap<String, LinkedList<String>>();

    /**
     * <p>List of user posts<p>
     */
    LinkedList<String> posts = new LinkedList<String>();

    /**
     * <p>Backup of previousl logged in users and their posts</p>
     */
    Map<String, LinkedList<String>> usersBackup = new HashMap<String, LinkedList<String>>();

    /**
     * <p>Checks whether a username is currently in use</p>
     * 
     * @param username username to check
     * @return whether a username is currently in use
     */
    public boolean validUsername(String username) {

        print("public boolean validUsername(String username = " + username + ")");
        return users.containsKey(username);
    }

    /**
     * <p>Log in, retrieving posts if previously logged out</p>
     *
     * @param username username to log in with
     * @return whether log in succeeded
     */
    public boolean login(String username) {

        print("public boolean login(String username = " + username + ")");
        if( ! validUsername(username)) {
            if(usersBackup.containsKey(username)) {
                LinkedList backup = usersBackup.get(username);
                users.put(username, backup);
            }
            else {
                users.put(username, new LinkedList<String>());
            }
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * <p>Log out, storing posts for retrieval if user logs in again</p>
     * 
     * @param username username to log out with
     * @return whether log out succeeded
     */
    public boolean logout(String username) {

        print("public boolean logout(String username = " + username + ")");
        if(validUsername(username)) {
            LinkedList backup = users.get(username);
            usersBackup.put(username, backup);
            users.remove(username);
            return true;
        }
        return false;
    }

    /**
     * <p>Post a message</p>
     * 
     * @param username username to post with
     * @param message users message
     * @return whether post successful
     */
    public boolean post(String username, String message) {

        print("public boolean post(String username = " + username + ", String message = " + message + ")");
        if(validUsername(username)) {
            LinkedList userPosts = users.get(username);
            userPosts.addFirst(message);
            posts.addFirst("<span>" + username + " says: </span><br>'" + message + "'");
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * <p>Get a List of posts for all users</p>
     * 
     * @param page page to retrieve
     * @param rows number of results to retrieve
     * @return List of posts
     */
    public List getPosts(int page, int rows) {

        print("public List getPosts(int page = " + page + ", int rows = " + rows + ")");
        int start = ((page + 1) * rows) - rows;
        int end = start + rows;
        if(start >= posts.size()) {
            return new LinkedList();
        }
        else if(end > posts.size()) {
            end = posts.size();
        }
        List selection = posts.subList(start, end);
        return selection;

    }

    /**
     * <p>Get a List for specific user</p>
     * 
     * @param username username to retrieve posts for
     * @param page page to retrieve
     * @param rows number of results to retrieve
     * @return List of posts
     * @throws Exception if username not logged in
     */
    public List getPosts(String username, int page, int rows) throws Exception {

        print("public List getPosts(String username = " + username + ", int page = " + page + ", int rows = " +
              rows + ")");
        if(validUsername(username)) {
            List userPosts = users.get(username);
            int start = ((page + 1) * rows) - rows;
            int end = start + rows;
            if(start >= userPosts.size()) {
                return new LinkedList();
            }
            else if(end > userPosts.size()) {
                end = userPosts.size();
            }
            List selection = userPosts.subList(start, end);
            return selection;
        }
        else {
            throw new Exception("Invalid Username: " + username);
        }
    }

    /**
     * <p>Get a List of all logged in users</p>
     * 
     * @param page page to retrieve
     * @param rows number of results to retrieve
     * @return List of usernames
     */
    public List getUsers(int page, int rows) {

        print("public List getUsers(int page = " + page + ", int rows = " + rows + ")");
        int start = ((page + 1) * rows) - rows;
        int end = start + rows;
        if(start >= users.size()) {
            return new LinkedList();
        }
        else if(end > users.size()) {
            end = users.size();
        }
        String[] keys = users.keySet().toArray(new String[users.size()]);
        List keysList = Arrays.asList(keys);
        return keysList.subList(start, end);

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
