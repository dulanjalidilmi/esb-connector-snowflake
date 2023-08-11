/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.connector.pojo;

import org.wso2.carbon.connector.utils.Error;

/**
 * Snowflakes operation result.
 */
public class SnowflakesOperationResult {
    /**
     * Snowflake Operation name
     */
    private String operation;

    /**
     * Is operation successfully executed
     */
    private boolean isSuccessful = false;

    /**
     * Error code
     */
    private Error error;

    /**
     * Error message
     */
    private String errorMessage;

    /**
     * Detailed Message
     */
    private String message;

    /**
     * SnowflakesOperationResult constructor
     *
     * @param operation     Snowflake Operation name
     * @param isSuccessful  Is operation successfully executed
     */
    public SnowflakesOperationResult(String operation, boolean isSuccessful) {
        this.operation = operation;
        this.isSuccessful = isSuccessful;
    }

    /**
     * SnowflakesOperationResult constructor
     *
     * @param operation     Snowflake Operation name
     * @param isSuccessful  Is operation successfully executed
     * @param message       Detailed Message
     */
    public SnowflakesOperationResult(String operation, boolean isSuccessful, String message) {
        this.operation = operation;
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    /**
     * SnowflakesOperationResult constructor
     *
     * @param operation     Snowflake Operation name
     * @param isSuccessful  Is operation successfully executed
     * @param error         Error code
     * @param errorMessage  Error message
     */
    public SnowflakesOperationResult(String operation, boolean isSuccessful, Error error, String errorMessage) {
        this.operation = operation;
        this.isSuccessful = isSuccessful;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    /**
     * Get the Snowflake Operation name
     *
     * @return Snowflake Operation name
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Get the detailed message
     *
     * @return detailed message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Is operation successfully executed
     *
     * @return true if operation is successfully executed
     */
    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Get the error code
     *
     * @return error code
     */
    public Error getError() {
        return error;
    }

    /**
     * Get the error message
     *
     * @return error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
