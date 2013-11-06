package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Iterator;
import com.netflix.paas.predicates.RelationPredicate;


/**
 * @author ssingh
 *
 */
public interface EntityIF {
    
    public Collection<RelationIF> getRelations(RelationTypeIF reltype, int max);
    /**
     * @param role
     * @param max
     * @return
     */
    //public Collection<PaasRelation> getPaasRelations(PaasRoleType role, int max);
    /**
     * @param trait
     */
    public <T> void addTrait(Trait<TraitType<T>, T> trait);
    
    /**
     * @param trait
     */
    public <T> void deleteTrait(Trait<TraitType<T>, T> trait);
    /**
     * @param type
     * @param max
     * @return
     */
    public <T> Collection<Trait<TraitType<T>, T>> getTraits(TraitType<T> type);
    /**
     * @param predicate
     * @param max
     * @return
     */
    public Collection<Trait<TraitType<?>, ?>> getTraits(EntityPredicate predicate);
    /**
     * @param max
     * @return
     */
    public Collection<TraitType<?>> getTraitTypes();
    /**
     * @param Trait
     * @param max
     * @return
     */
    public <T> Collection<T> getTraitValues(Trait<TraitType<T>, T> trait, int max);
    /**
     * @param max
     * @return
     */
    public EntityId getId();
    /**
     * @param max
     * @return
     */
    public Collection<RelationIF> getRelations();
    /**
     * @param predicate
     * @param max
     * @return
     */
    public Collection<RelationIF> getRelations(RelationPredicate predicate);
    /**
     * @param role
     * @param max
     * @return
     */
    public Collection<RelationIF> getRelations(RoleTypeIF role, int max);
    /**
     * @return
     */
    public EntityTypeIF getType();
    /**
     * @return
     */
    public String getName();
    /**
     * @param type
     * @return
     */
    public <T> Iterator<Trait<TraitType<T>, T>> iteratorForTraits(TraitType<T> type);
    /**
     * @param type
     * @return
     */
    public <T> Iterator<Trait<TraitType<T>, T>> iteratorForTraits(EntityPredicate type);
    /**
     * @return
     */
    public Iterator<TraitType<?>> iteratorForTraitTypes();
    /**
     * @return
     */
    public Iterator<RelationIF> iteratorForRelations();
    /**
     * @param predicate
     * @return
     */
    public Iterator<RelationIF> iteratorForRelations(RelationPredicate predicate);
    /**
     * @param role
     * @return
     */
    public Iterator<RelationIF> iteratorForRelations(RoleTypeIF role);
}
