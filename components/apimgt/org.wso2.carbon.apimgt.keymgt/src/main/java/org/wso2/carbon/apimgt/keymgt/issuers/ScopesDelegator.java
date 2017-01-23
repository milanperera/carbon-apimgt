/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.keymgt.issuers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.issuers.ScopesIssuer;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;

import java.util.*;

public class ScopesDelegator {

    private static Log log = LogFactory.getLog(ScopesDelegator.class);
    private List<String> scopeSkipList = new ArrayList<String>();
    private static Map<String, ScopesIssuer> scopesIssuers;
    private static final String DEFAULT_SCOPE_NAME = "default";
    /**
     * Singleton of ScopeIssuer.*
     */
    private static ScopesDelegator scopesDelegator;
    
    private ScopesDelegator() {
    }

    public static void loadInstance(List<String> whitelist) {
        scopesDelegator = new ScopesDelegator();
        if (whitelist != null && !whitelist.isEmpty()) {
            scopesDelegator.scopeSkipList.addAll(whitelist);
        }
        scopesIssuers = OAuthServerConfiguration.getInstance().getOAuth2ScopesIssuers();
    }  

    public static ScopesDelegator getInstance() {
        return scopesDelegator;
    }

    public boolean setScopes(OAuthTokenReqMessageContext tokReqMsgCtx) {

        String[] requestedScopes = tokReqMsgCtx.getScope();
        String[] defaultScope = new String[]{DEFAULT_SCOPE_NAME};

        // if no issuers are defined
        if (scopesIssuers == null || scopesIssuers.isEmpty()) {

            if (log.isDebugEnabled()) {
                log.debug("Scope Issuers are not defined in 'identity.xml'");
            }
            tokReqMsgCtx.setScope(defaultScope);
            return true;
        }

        //If no scopes were requested.
        if (requestedScopes == null || requestedScopes.length == 0) {
            tokReqMsgCtx.setScope(defaultScope);
            return true;
        }
        //List<String> reqScopeList = Arrays.asList(requestedScopes);

        Map<String, List<String>> scopeSets = new HashMap<String, List<String>>();

        // initializing scope sets with respect to prefixes
        for (String prefix : scopesIssuers.keySet()) {
            scopeSets.put(prefix, new ArrayList<String>());
        }

        for (String scope : requestedScopes) {
            for (String prefix : scopesIssuers.keySet()) {
                if (scope.startsWith(prefix)) {
                    scopeSets.get(prefix).add(scope);
                    break;
                }
                scopeSets.get(DEFAULT_SCOPE_NAME).add(scope);
            }
        }

        List<String> authorizedAllScopes = new ArrayList<String>();
        List<String> authorizedScopes;
        boolean isAllAuthorized = false;
        for (String prefix : scopesIssuers.keySet()) {
            authorizedScopes = scopesIssuers.get(prefix).getScopes(tokReqMsgCtx, scopeSets.get(prefix));
            if (authorizedAllScopes != null) {
                authorizedAllScopes.addAll(authorizedScopes);
                isAllAuthorized = true;
            } else {
                isAllAuthorized = false;
            }
        }

        if (isAllAuthorized) {
            tokReqMsgCtx.setScope((String[]) authorizedAllScopes.toArray());
            return true;
        }
        return false;
    }

}


