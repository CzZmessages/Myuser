package com.hongri.multimedia.bean;

import java.io.Serializable;

public class UserMsgBean implements Serializable {
    private String id;
    private String username;
    private boolean isVisitor;
    private String email;
    private String role;
    private String[] permissions;
    private boolean isEditPassword;

    public UserMsgBean() {
    }

    public UserMsgBean(String id, String username, boolean isVisitor, String email, String role, String[] permissions, boolean isEditPassword) {
        this.id = id;
        this.username = username;
        this.isVisitor = isVisitor;
        this.email = email;
        this.role = role;
        this.permissions = permissions;
        this.isEditPassword = isEditPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isVisitor() {
        return isVisitor;
    }

    public void setVisitor(boolean visitor) {
        isVisitor = visitor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public boolean isEditPassword() {
        return isEditPassword;
    }

    public void setEditPassword(boolean editPassword) {
        isEditPassword = editPassword;
    }
}
