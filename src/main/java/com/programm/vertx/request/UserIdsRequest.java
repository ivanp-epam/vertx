package com.programm.vertx.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdsRequest {

    private List<String> userIds;

    public List<String> getUserIds() {
        return userIds;
    }

    public UserIdsRequest setUserIds(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }
}
