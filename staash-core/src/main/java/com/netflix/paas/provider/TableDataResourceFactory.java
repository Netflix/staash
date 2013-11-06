package com.netflix.paas.provider;

import com.netflix.paas.entity.TableEntity;
import com.netflix.paas.exceptions.NotFoundException;
import com.netflix.paas.resources.TableDataResource;

/**
 * Provides a REST resource that can query the table specified by the entity
 * 
 * @author elandau
 */
public interface TableDataResourceFactory {
    TableDataResource getTableDataResource(TableEntity entity) throws NotFoundException;
}
