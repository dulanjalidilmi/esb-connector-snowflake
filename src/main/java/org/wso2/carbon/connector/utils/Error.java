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

package org.wso2.carbon.connector.utils;

/**
 * Contains error codes and details related to snowflake connector
 */
public enum Error {

    CONNECTION_ERROR("700601", "SNOWFLAKE:CONNECTION_ERROR"),
    INVALID_CONFIGURATION("700602", "SNOWFLAKE:INVALID_CONFIGURATION"),
    OPERATION_ERROR("700603", "SNOWFLAKE:OPERATION_ERROR"),
    RESPONSE_GENERATION("700604", "EMAIL:RESPONSE_GENERATION");

    private final String code;
    private final String message;

    /**
     * Create an error code.
     *
     * @param code    error code represented by number
     * @param message error message
     */
    Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Get error code.
     *
     * @return error code
     */
    public String getErrorCode() {
        return this.code;
    }

    /**
     * Get error message.
     *
     * @return error message
     */
    public String getErrorDetail() {
        return this.message;
    }
    
}
