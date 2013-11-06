package com.netflix.paas.entity;

import java.util.List;

public class ColumnMetadataEntity {
    private String type;
    private String name;
    private List<ColumnValidatorEntity> validator;
    private List<ColumnIndexEntity> indexes;
}
