package com.lyq.common.model;

import java.io.Serializable;

/**
 * 用户类
 * @author lyq
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
