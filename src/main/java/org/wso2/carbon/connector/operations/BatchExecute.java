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

package org.wso2.carbon.connector.operations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.connection.SnowflakeConnection;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.connection.ConnectionHandler;
import org.wso2.carbon.connector.exception.InvalidConfigurationException;
import org.wso2.carbon.connector.exception.SnowflakeOperationException;
import org.wso2.carbon.connector.pojo.SnowflakesOperationResult;
import org.wso2.carbon.connector.utils.Constants;
import org.wso2.carbon.connector.utils.Error;
import org.wso2.carbon.connector.utils.SnowflakeUtils;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implements batch execute operation.
 */
public class BatchExecute extends AbstractConnector {

    private static final String OPERATION_NAME = "batchExecute";
    private static final String ERROR_MESSAGE = "Error occurred while performing snowflake:batchExecute operation.";

    @Override
    public void connect(MessageContext messageContext) {
        ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
        String connectionName = null;
        SnowflakeConnection snowflakeConnection = null;

        try {
            connectionName = SnowflakeUtils.getConnectionName(messageContext);
            snowflakeConnection = (SnowflakeConnection) handler.getConnection(Constants.CONNECTOR_NAME, connectionName);
            SnowflakesOperationResult result = batchExecuteQuery(messageContext, snowflakeConnection);
            SnowflakeUtils.setResultAsPayload(messageContext, result);
        } catch (InvalidConfigurationException e) {
            handleError(messageContext, e, Error.INVALID_CONFIGURATION, ERROR_MESSAGE);
        } catch (SnowflakeOperationException e) {
            handleError(messageContext, e, Error.OPERATION_ERROR, ERROR_MESSAGE);
        } catch (ConnectException e) {
            handleError(messageContext, e, Error.CONNECTION_ERROR, ERROR_MESSAGE);
        } finally {
            if (snowflakeConnection != null) {
                handler.returnConnection(Constants.CONNECTOR_NAME, connectionName, snowflakeConnection);
            }
        }
    }

    /**
     * Executes the query.
     *
     * @param messageContext      Message Context.
     * @param snowflakeConnection Snowflake Connection.
     * @return SnowflakesOperationResult.
     * @throws InvalidConfigurationException Invalid Configuration Exception.
     * @throws SnowflakeOperationException  Snowflake Operation Exception.
     */
    private SnowflakesOperationResult batchExecuteQuery(MessageContext messageContext, SnowflakeConnection snowflakeConnection)
            throws InvalidConfigurationException, SnowflakeOperationException {
        SnowflakesOperationResult snowflakesOperationResult =
                new SnowflakesOperationResult(OPERATION_NAME, false);
        String query = (String) getParameter(messageContext, Constants.EXECUTE_QUERY);
        String payload = (String) getParameter(messageContext, Constants.PAYLOAD);

        if (StringUtils.isEmpty(query)) {
            throw new InvalidConfigurationException("Execute Query is not provided.");
        }

        if (StringUtils.isEmpty(payload)) {
            throw new InvalidConfigurationException("Empty Payload is provided.");
        }

        JsonArray insertArray = JsonParser.parseString(payload).getAsJsonArray();
        String[] columns = SnowflakeUtils.getColumnNames(query);
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = snowflakeConnection.getConnection().prepareStatement(query);
            if (insertArray.size()>0 && columns.length>0) {
                for (int i = 0; i<insertArray.size(); i++) {
                    JsonObject insertObject = insertArray.get(i).getAsJsonObject();
                    int increment = 0;
                    for (String column : columns) {
                        JsonElement value = insertObject.get(column);
                        preparedStatement.setString(++increment, (value != null) ? value.getAsString() : "");
                    }
                    preparedStatement.addBatch();
                }

                int[] batchResult = preparedStatement.executeBatch();
                int successfulCount = 0;
                for (int updateCount : batchResult) {
                    if (updateCount >= 0) {
                        successfulCount++;
                    }
                }

                if (batchResult.length > 0) {
                    String message = "Successfully executed " + successfulCount + " statements";
                    snowflakesOperationResult = new SnowflakesOperationResult(OPERATION_NAME, true, message);
                }
            }
            return snowflakesOperationResult;
        } catch (SQLException e) {
            throw new SnowflakeOperationException("Error occurred while executing the query.", e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                String error = String.format("%s:%s Error while closing the prepared statement.",
                        Constants.CONNECTOR_NAME, OPERATION_NAME);
                log.error(error, e);
            }
        }
    }

    /**
     * Set error to context and handle.
     *
     * @param messageContext Message Context.
     * @param e              Exception.
     * @param error          Error.
     * @param errorDetail    Error Detail.
     */
    private void handleError(MessageContext messageContext, Exception e, Error error, String errorDetail) {
        SnowflakeUtils.setError(OPERATION_NAME, messageContext, e, error);
        handleException(errorDetail, e, messageContext);
    }
}
