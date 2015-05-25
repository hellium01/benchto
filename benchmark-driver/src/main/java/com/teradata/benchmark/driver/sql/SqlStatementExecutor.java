/*
 * Copyright 2013-2015, Teradata, Inc. All rights reserved.
 */
package com.teradata.benchmark.driver.sql;

import com.teradata.benchmark.driver.BenchmarkQuery;
import com.teradata.benchmark.driver.sql.QueryExecution.QueryExecutionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SqlStatementExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger(SqlStatementExecutor.class);

    @Autowired
    private DataSource dataSource;

    public QueryExecution executeQuery(BenchmarkQuery benchmarkQuery)
    {
        LOG.debug("Executing query {}: {}", benchmarkQuery.getName(), benchmarkQuery.getSql());

        QueryExecutionBuilder queryExecutionBuilder = new QueryExecutionBuilder()
                .startTimer();
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(benchmarkQuery.getSql())
        ) {
            int rowsCount = 0;
            if (resultSet.next()) {
                rowsCount++;
            }

            queryExecutionBuilder.setRowsCount(rowsCount);
        }
        catch (SQLException e) {
            queryExecutionBuilder.failed(e);
        }

        return queryExecutionBuilder
                .endTimer()
                .build();
    }
}