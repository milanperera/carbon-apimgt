/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
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

package org.wso2.carbon.apimgt.api.model.policy;

public class SubscriptionPolicy extends Policy {
    private int rateLimitCount;
    private String rateLimitTimeUnit;

    public SubscriptionPolicy(String name) {
        super(name);
    }

    public int getRateLimitCount() {
        return rateLimitCount;
    }

    public void setRateLimitCount(int rateLimitCount) {
        this.rateLimitCount = rateLimitCount;
    }

    public String getRateLimitTimeUnit() {
        return rateLimitTimeUnit;
    }

    public void setRateLimitTimeUnit(String rateLimitTimeUnit) {
        this.rateLimitTimeUnit = rateLimitTimeUnit;
    }

    @Override
    public String toString() {
        return "SubscriptionPolicy [policyName=" + getPolicyName()
                + ", description=" + getDescription() + ", defaultQuotaPolicy=" + getDefaultQuotaPolicy() +
                "rateLimitCount=" + rateLimitCount + ", tenantId=" + getTenantId() + ", ratelimitTimeUnit=" + rateLimitTimeUnit + "]";
    }
}