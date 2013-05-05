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

/**
 * jQuery RPC plugin.
 * jQuery RPC plugin is a JSON RPC client for RpcServlet.
 * 
 * @author Gareth Bond
 */
(function($){

    /**
     * Namespace for this plugin.
     */
    $.rpc = {};

    /**
     * Param name for arguments array.
     */
    $.rpc.ARGS_PARAM = 'a';

    /**
     * Response format.
     */
    $.rpc.RESP_TYPE = 'json';

    /**
     * Invoke a method with the supplied params.
     * If remote method takes no params use null.
     * If remote method takes multiple arguments these must be supplied as an array.
     * If remote method takes a single array argument this must be supplied within another array.
     *
     * @param url, URL of the RPC service
     * @param method, method name to invoke
     * @param params, params to pass to the remote method
     * @param callback, function to handle the response
     */
    $.rpc.invoke = function(url, method, params, callback) {

        //add forward slash if needed
        if(url.charAt(url.length -1) != '/') url += '/';

        //append method to url is needed
        if(method != null) url += method;

        //data to post
        var data = {};

        //add params to data if present
        if(params != null) {

            //params must be an array
            if(!(params instanceof Array)) params = [params];
            data[this.ARGS_PARAM] = JSON.stringify(params);
        }

        //execute HTTP POST request
        $.post(url, data, callback, this.RESP_TYPE);
    };
    
})(jQuery);