package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Iterator;

import com.netflix.paas.predicates.RelationPredicate;

public  class PaasEntity implements EntityIF{
    private int Id;
    private String name;
    private Collection<Trait> attributes;
    private Collection<RelationIF> relations;
    @Override
    public Collection<RelationIF> getRelations(RelationTypeIF reltype, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> void addTrait(Trait<TraitType<T>, T> trait) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public <T> void deleteTrait(Trait<TraitType<T>, T> trait) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public <T> Collection<Trait<TraitType<T>, T>> getTraits(TraitType<T> type) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Collection<Trait<TraitType<?>, ?>> getTraits(
            EntityPredicate predicate) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Collection<TraitType<?>> getTraitTypes() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> Collection<T> getTraitValues(Trait<TraitType<T>, T> trait,
            int max) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public EntityId getId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Collection<RelationIF> getRelations() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Collection<RelationIF> getRelations(RelationPredicate predicate) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Collection<RelationIF> getRelations(RoleTypeIF role, int max) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public EntityTypeIF getType() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> Iterator<Trait<TraitType<T>, T>> iteratorForTraits(
            TraitType<T> type) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T> Iterator<Trait<TraitType<T>, T>> iteratorForTraits(
            EntityPredicate type) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Iterator<TraitType<?>> iteratorForTraitTypes() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Iterator<RelationIF> iteratorForRelations() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Iterator<RelationIF> iteratorForRelations(RelationPredicate predicate) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Iterator<RelationIF> iteratorForRelations(RoleTypeIF role) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
