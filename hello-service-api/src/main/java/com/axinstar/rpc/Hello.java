package com.axinstar.rpc;

import java.io.Serializable;

/**
 * @author axin
 * @since 2024/03/30
 */
public class Hello implements Serializable {

    private String message;
    private String description;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Hello(String message, String description) {
        this.message = message;
        this.description = description;
    }
}
