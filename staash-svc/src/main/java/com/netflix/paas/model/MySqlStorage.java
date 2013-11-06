package com.netflix.paas.model;

import com.netflix.paas.json.JsonObject;

public class MySqlStorage extends Storage{
    private String jdbcurl;
    private String user;
    private String password;
    private String host;
    public MySqlStorage(JsonObject conf) {
        this.host = conf.getString("host");
        this.jdbcurl = conf.getString("jdbcurl");
        this.name =  conf.getString("name");
        this.user = conf.getString("user");
        this.password = conf.getString("password");
    }
    public String getJdbcurl() {
        return jdbcurl;
    }
    public String getUser() {
        return user;
    }
    public String getPassword() {
        return password;
    }
    public String getHost() {
        return host;
    }
}
