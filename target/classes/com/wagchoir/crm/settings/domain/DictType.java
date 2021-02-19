package com.wagchoir.crm.settings.domain;

public class DictType {
    private String code;//相当于id 非空+唯一
    private String name;//类别 如性别、部门、城市
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
