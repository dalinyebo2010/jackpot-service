package com.jackpot.controller;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DBDataController {

    private final DatabaseClient client;

    public DBDataController(DatabaseClient client) {
        this.client = client;
    }

    @GetMapping("/all")
    public Flux<Map<String, Object>> getAllTablesData() {
        // First query all user tables from H2’s INFORMATION_SCHEMA
        return client.sql("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'")
                .fetch()
                .all()
                .flatMap(row -> {
                    String tableName = (String) row.get("TABLE_NAME");
                    // For each table, run SELECT * and wrap results with table name
                    return client.sql("SELECT * FROM " + tableName).fetch().all()
                            .map(dataRow -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("table", tableName);
                                result.put("row", dataRow);
                                return result;
                            });
                });
    }
}
