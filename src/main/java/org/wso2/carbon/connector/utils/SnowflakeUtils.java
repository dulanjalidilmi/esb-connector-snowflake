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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.exception.InvalidConfigurationException;
import org.wso2.carbon.connector.pojo.SnowflakesOperationResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util methods related to snowflake connector operations
 */
public class SnowflakeUtils {

    private static final Log log = LogFactory.getLog(SnowflakeUtils.class);

    /**
     * Get the connection name from the message context
     *
     * @param messageContext
     * @return connection name
     * @throws InvalidConfigurationException
     */
    public static String getConnectionName(MessageContext messageContext) throws InvalidConfigurationException {
        String connectionName = (String) messageContext.getProperty(Constants.CONNECTION_NAME);
        if (StringUtils.isEmpty(connectionName)) {
            throw new InvalidConfigurationException("Connection name is not set.");
        }
        return connectionName;
    }

    /**
     * Set the result as the payload in the message context
     *
     * @param msgContext
     * @param result
     */
    public static void setResultAsPayload(MessageContext msgContext, SnowflakesOperationResult result) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", result.getOperation());
        jsonObject.addProperty("isSuccessful", result.isSuccessful());

        if (result.getMessage() != null && !result.getMessage().isEmpty()) {
            jsonObject.addProperty("message", result.getMessage());
        }

        if (result.getError() != null) {
            setErrorPropertiesToMessageContext(msgContext, result.getError());
            jsonObject.addProperty("error", result.getError().getErrorCode());
            jsonObject.addProperty("code", result.getError().getErrorCode());
            jsonObject.addProperty("errorDetail", result.getError().getErrorDetail());
        }

        org.apache.axis2.context.MessageContext axisMsgCtx =
                ((Axis2MessageContext) msgContext).getAxis2MessageContext();

        JsonUtil.removeJsonPayload(axisMsgCtx);
        axisMsgCtx.removeProperty("NO_ENTITY_BODY");
        try {
            JsonUtil.getNewJsonPayload(axisMsgCtx, jsonObject.toString(), true, true);
        } catch (AxisFault axisFault) {
            log.error("Error occurred while populating the payload in the message context.", axisFault);
        }
    }

    /**
     * Set the result as the payload in the message context
     *
     * @param msgContext message context
     * @param result the result array to be set as the payload
     */
    public static void setResultAsPayload(MessageContext msgContext, JsonArray result) {
        org.apache.axis2.context.MessageContext axisMsgCtx =
                ((Axis2MessageContext) msgContext).getAxis2MessageContext();
        JsonUtil.removeJsonPayload(axisMsgCtx);
        axisMsgCtx.removeProperty("NO_ENTITY_BODY");
        try {
            JsonUtil.getNewJsonPayload(axisMsgCtx, result.toString(), true, true);
        } catch (AxisFault e) {
            log.error("Error occurred while populating the payload in the message context.", e);
        }
    }

    /**
     * Set error properties to the message context
     *
     * @param msgContext
     * @param error
     */
    private static void setErrorPropertiesToMessageContext(MessageContext msgContext, Error error) {
        msgContext.setProperty(Constants.PROPERTY_ERROR_CODE, error.getErrorCode());
        msgContext.setProperty(Constants.PROPERTY_ERROR_MESSAGE, error.getErrorDetail());
        Axis2MessageContext axis2smc = (Axis2MessageContext) msgContext;
        org.apache.axis2.context.MessageContext axis2MessageCtx = axis2smc.getAxis2MessageContext();
        axis2MessageCtx.setProperty(Constants.STATUS_CODE, Constants.HTTP_STATUS_500);
    }

    /**
     * Get the column names in an array from the query
     *
     * @param query
     * @return column names
     * @throws InvalidConfigurationException
     */
    public static String[] getColumnNames(String query) throws InvalidConfigurationException {
        // Using regular expression to match the column names inside the parentheses
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(query);
        String[] columnNames = null;

        if (matcher.find()) {
            String columnNamesStr = matcher.group(1);
            columnNames = columnNamesStr.split("\\s*,\\s*");
        }

        if (columnNames == null) {
            throw new InvalidConfigurationException(
                    "Invalid query is provided. Column names are not found in the query.");
        }

        return columnNames;
    }

    /**
     * Get the json object from the payload
     *
     * @param payload
     * @return json object
     */
    public static JsonObject getJsonObject(String payload) {
        Gson gson = new Gson();
        return gson.fromJson(payload, JsonObject.class);
    }

    /**
     * Set error to the message context
     *
     * @param operation operation name
     * @param messageContext message context
     * @param e exception
     * @param error Error object
     */
    public static void setError(String operation, MessageContext messageContext, Exception e, Error error) {
        SnowflakesOperationResult result =
                new SnowflakesOperationResult(operation, false, error, e.getMessage());
        setResultAsPayload(messageContext, result);
    }

}
