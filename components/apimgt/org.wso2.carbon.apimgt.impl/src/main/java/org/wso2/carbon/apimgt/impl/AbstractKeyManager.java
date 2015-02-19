/*
 *
 *   Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.apimgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.AccessTokenRequest;
import org.wso2.carbon.apimgt.api.model.ApplicationConstants;
import org.wso2.carbon.apimgt.api.model.KeyManager;
import org.wso2.carbon.apimgt.api.model.OAuthApplicationInfo;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;

import java.util.Map;

/**
 * Mostly common features of  keyManager implementations will be handle here.
 * This class should be extended by Key manager implementation class.
 */
public abstract class AbstractKeyManager implements KeyManager {
    private static Log log = LogFactory.getLog(AbstractKeyManager.class);

    public AccessTokenRequest buildAccessTokenRequestFromJSON(String jsonInput, AccessTokenRequest tokenRequest)
            throws APIManagementException {

        if (jsonInput == null || jsonInput.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("JsonInput is null or Empty.");
            }
            return tokenRequest;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;

        if (tokenRequest == null) {
            if (log.isDebugEnabled()) {
                log.debug("Input request is null. Creating a new Request Object.");
            }
            tokenRequest = new AccessTokenRequest();
        }

        try {
            jsonObject = (JSONObject) parser.parse(jsonInput);
            // Getting parameters from input string and setting in TokenRequest.
            if (jsonObject instanceof Map && !jsonObject.isEmpty()) {
                Map<String, Object> params = (Map) jsonObject;

                if (null != params.get(ApplicationConstants.OAUTH_CLIENT_ID)) {
                    tokenRequest.setClientId((String) params.get(ApplicationConstants.OAUTH_CLIENT_ID));
                }

                if (null != params.get(ApplicationConstants.OAUTH_CLIENT_SECRET)) {
                    tokenRequest.setClientSecret((String) params.get(ApplicationConstants.OAUTH_CLIENT_SECRET));
                }

                return tokenRequest;
            }
        } catch (ParseException e) {
            handleException("Error occurred while parsing JSON String", e);
        }
        return null;
    }


    /**
     * This method will accept json String and will do the json parse will set oAuth application properties to OAuthApplicationInfo object.
     *
     * @param jsonInput this jsonInput will contain set of oAuth application properties.
     * @return OAuthApplicationInfo object will be return.
     * @throws APIManagementException
     */
    public OAuthApplicationInfo buildFromJSON(String jsonInput) throws APIManagementException {
        //initiate json parser.
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;

        try {
            //parse json String
            jsonObject = (JSONObject) parser.parse(jsonInput);
            if (jsonObject instanceof Map) {
                //create a map to hold json parsed objects.
                Map<String, Object> params = (Map) jsonObject;
                //initiate OAuthApplicationInfo object.
                OAuthApplicationInfo info = new OAuthApplicationInfo();
                //set client Id
                if ((String) params.get("client_id") != null) {
                    info.setClientId((String) params.get("client_id"));
                }
                //copy all params map in to OAuthApplicationInfo's Map object.
                info.putAll(params);
                return info;
            }
        } catch (ParseException e) {
            handleException("Error occurred while parsing JSON String", e);
        }
        return null;
    }

    public AccessTokenRequest buildAccessTokenRequestFromOAuthApp(OAuthApplicationInfo oAuthApplication,
                                                           AccessTokenRequest tokenRequest) throws
                                                                                            APIManagementException{
        if(oAuthApplication == null){
            return tokenRequest;
        }
        if(tokenRequest == null){
            tokenRequest = new AccessTokenRequest();
        }
        tokenRequest.setClientId(oAuthApplication.getClientId());
        tokenRequest.setClientSecret((String) oAuthApplication.getParameter(ApplicationConstants.OAUTH_CLIENT_SECRET));

        return tokenRequest;
    }

    /**
     * common method to throw exceptions.
     *
     * @param msg this parameter contain error message that we need to throw.
     * @param e   Exception object.
     * @throws APIManagementException
     */
    private void handleException(String msg, Exception e) throws APIManagementException {
        log.error(msg, e);
        throw new APIManagementException(msg, e);
    }
}
