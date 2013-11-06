package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public  class PaasRelation implements RelationIF {
    private int id;
    private String  name;
    private Map<EntityTypeIF, EntityIF> players;

    @Override
    public void addPlayers(EntityTypeIF role, EntityIF... subjects) {
        // TODO Auto-generated method stub

    }

    @Override
    public RelationId getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<EntityIF> getPlayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<EntityIF> getPlayers(EntityTypeIF entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<EntityIF> getPlayers(EntityTypeIF entityType, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RelationTypeIF getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<EntityIF> iteratorForPlayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<EntityIF> iteratorForPlayers(EntityTypeIF roleType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void remove(EntityTypeIF role) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(EntityTypeIF role, EntityIF... players) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(EntityIF player) {
        // TODO Auto-generated method stub

    }

}
