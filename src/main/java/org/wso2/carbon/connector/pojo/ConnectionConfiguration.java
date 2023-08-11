/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.carbon.connector.pojo;

import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.connector.exception.InvalidConfigurationException;

/**
 * Initializes the snowflake connection based on the provided configs at config/init.xml.
 * Required input validations also done here.
 */
public class ConnectionConfiguration {

    private String connectionName;
    private String accountIdentifier;
    private String user;
    private String password;

    /**
     * Get the connection name for the snowflake connection.
     *
     * @return connectionName
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Set the connection name for the snowflake connection.
     *
     * @param connectionName
     */
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    /**
     * Get the account identifier for the snowflake connection.
     *
     * @return accountIdentifier
     */
    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    /**
     * Set the account identifier for the snowflake connection.
     *
     * @param accountIdentifier
     * @throws InvalidConfigurationException
     */
    public void setAccountIdentifier(String accountIdentifier) throws InvalidConfigurationException {
        if (StringUtils.isEmpty(accountIdentifier)) {
            throw new InvalidConfigurationException("Mandatory parameter 'accountIdentifier' is not set.");
        }
        this.accountIdentifier = "jdbc:snowflake://".concat(accountIdentifier).concat(".snowflakecomputing.com/");
    }

    /**
     * Get the user for the snowflake connection.
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the user for the snowflake connection.
     *
     * @param user
     * @throws InvalidConfigurationException
     */
    public void setUser(String user) throws InvalidConfigurationException {
        if (StringUtils.isEmpty(user)) {
            throw new InvalidConfigurationException("Mandatory parameter 'user' is not set.");
        }
        this.user = user;
    }

    /**
     * Get the password for the snowflake connection.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for the snowflake connection.
     *
     * @param password
     * @throws InvalidConfigurationException
     */
    public void setPassword(String password) throws InvalidConfigurationException {
        if (StringUtils.isEmpty(password)) {
            throw new InvalidConfigurationException("Mandatory parameter 'password' is not set.");
        }
        this.password = password;
    }
}
