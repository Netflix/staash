package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import com.netflix.paas.predicates.AllEntityPredicate;
import com.netflix.paas.predicates.RelationPredicate;

/**
 * @author ssingh
 *
 */
public interface EntityContextIF {   
    /**
     * @param relationType
     * @return
     * @throws EntityException
     */
    public RelationIF createRelation(String name, RelationTypeIF relationType) throws EntityException;
    /**
     * @param relationType
     * @return
     * @throws EntityException
     */
    public RelationTypeIF createRelationType(String typeName) throws EntityException;
    /**
     * @param roleType
     * @return
     * @throws PaasEntityException
     */
//    public PaasRoleType createRoleType(String roleType) throws PaasEntityException;
    /**
     * @param type
     * @return
     * @throws EntityException
     * 
     */
    public EntityIF createEntity(EntityTypeIF entityType) throws EntityException;
    /**
     * @param entityType
     * @return
     * @throws EntityException
     */
    public EntityTypeIF createEntityType(String typeName) throws EntityException;
    /**
     * @param name
     * @return
     */
    public <T> TraitType<T> getTraitType(String name);
    /**
     * @param predicate
     * @param maximumNumber
     * @return
     * @throws EntityException
     */
    public Collection<RelationId> getRelationIds(RelationPredicate predicate)
            throws EntityException;
    /**
     * @param predicate
     * @param maximumNumber
     * @return
     * @throws EntityException
     */
    public Collection<RelationIF> getRelations(RelationPredicate predicate)
            throws EntityException;
    /**
     * @param relationType
     * @return
     * @throws EntityException
     */
    public RelationTypeIF getRelationType(String relationType) throws EntityException;
    /**
     * @param roleType
     * @return
     * @throws PaasEntityException
     */
//    public PaasRoleType getRoleType(String roleType) throws PaasEntityException;
    /**
     * @param root
     * @return
     */
    public Map<EntityTypeIF, Collection<EntityIF>> getEntitiesSubTree(EntityIF root);
    /**
     * @param predicate
     * @param maximumNumber
     * @return
     * @throws EntityException
     */
    public Collection<EntityId> getEntityIds(AllEntityPredicate predicate)
            throws EntityException;
    /**
     * @param predicate
     * @param maximumNumber
     * @return
     * @throws EntityException
     */
    public Collection<EntityIF> getPaasEntities(AllEntityPredicate predicate) throws EntityException;
    /**
     * @param entityType
     * @return
     * @throws EntityException
     */
    public EntityTypeIF getEntityType(String entityType) throws EntityException;
    /**
     * @return
     */
    public boolean hasChanges();
    /**
     * @param rel
     * @return
     * @throws EntityException
     */
    public RelationIF instantiateInContext(RelationIF rel) throws EntityException;
    /**
     * @param entity
     * @return
     * @throws EntityException
     */
    public EntityIF instantiateInContext(EntityIF entity) throws EntityException;
    /**
     * @param predicate
     * @return
     * @throws EntityException
     */
    public Iterator<RelationId> iteratorForPaasRelationIds(RelationPredicate predicate) throws EntityException;
    /**
     * @param predicate
     * @return
     * @throws EntityException
     */
    public Iterator<RelationIF> iteratorForPaasRelations(RelationPredicate predicate) throws EntityException;
    /**
     * @param predicate
     * @return
     * @throws EntityException
     */
    public Iterator<EntityId> iteratorForPaasEntityIds(EntityPredicate predicate) throws EntityException;
    /**
     * @param predicate
     * @return
     * @throws EntityException
     */
    public Iterator<EntityIF> iteratorForPaasEntities(EntityPredicate predicate) throws EntityException;
    /**
     * @throws EntityException
     */
    public void commit() throws EntityException;
    
    /**
     * @param traitType
     * @param type
     * @return
     */
    public <T> Trait<TraitType<T>, T> createTrait(TraitType<T> traitType, T value);
    /**
     * @param name
     * @return
     */
    public <T> TraitType<T> createTraitType(String name, Class<T> type);
    /**
     * 
     */
    public void reset();
}
