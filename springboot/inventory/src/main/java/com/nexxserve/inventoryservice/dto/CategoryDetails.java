package com.nexxserve.inventoryservice.dto;

import lombok.Data;

@Data
public class CategoryDetails {
    private String id;
    private String name;
    private String code;
    private String description;
    private Integer level;
    private String parentId;
    private Integer displayOrder;
    private boolean isActive;
    private String icon;
    private String taxCategory;
    private String regulatoryCategory;
}
