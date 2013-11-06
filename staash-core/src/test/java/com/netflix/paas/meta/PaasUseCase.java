package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.netflix.paas.predicates.AllEntityPredicate;
import com.netflix.paas.predicates.EntityTypePredicate;
import com.netflix.paas.predicates.TraitPredicate;
import com.netflix.paas.predicates.PredicateCompare;
import com.netflix.paas.resources.DataResource;

public class PaasUseCase {
    private static final String CASSANDRA_KEYSPACE_ENTITY_TYPE = "com.test.entity.type.cassandra.keyspace";
    private static final String PAAS_TABLE_ENTITY_TYPE = "com.test.entity.type.paas.table";
    private static final String PAAS_DB_ENTITY_TYPE = "com.test.entity.type.paas.db";
    private static final String CASSANDRA_CF_TYPE = "com.test.entity.type.cassandra.columnfamily";
    private static final String CASSANDRA_TIMESERIES_TYPE = "com.test.entity.type.cassandra.timeseries";
    private static final String PAAS_CLUSTER_ENTITY_TYPE = "com.test.entity.type.paas.table";
    private static final String STORAGE_TYPE = "com.test.trait.type.storagetype";
    private static final String RESOLUTION_TYPE = "com.test.trait.type.resolutionstring";
    private static final String NAME_TYPE = "com.test.trait.type.name";
    private static final String RF_TYPE = "com.test.trait.type.replicationfactor";
    private static final String STRATEGY_TYPE = "com.test.trait.type.strategy";
    private static final String COMPARATOR_TYPE = "com.test.trait.type.comparator";
    private static final String KEY_VALIDATION_CLASS_TYPE = "com.test.trait.type.key_validation_class";
    private static final String COLUMN_VALIDATION_CLASS_TYPE = "com.test.trait.type.validation_class";
    private static final String DEFAULT_VALIDATION_CLASS_TYPE = "com.test.trait.type.default_validation_class";
    private static final String COLUMN_NAME_TYPE = "com.test.trait.type.colum_name";
    private static final String CONTAINS_TYPE = "com.test.relation.type.contains";
    private EntityTypeIF keySpaceType;
    private EntityTypeIF columnFamilyType;
    private EntityTypeIF timeSeriesType;
    private EntityTypeIF tableType;
    private EntityTypeIF clusterType;
    private EntityTypeIF dbType;
    private TraitType<String> storageType;
    private TraitType<String> resolutionType; 
    private TraitType<String> nameType;
    private TraitType<Integer> replicationType;
    private TraitType<String> strategyType;
    private TraitType<String> comparatorType;
    private TraitType<String> keyValidationType;
    private TraitType<String> columnValidationType;
    private TraitType<String> defaultValidationType;
    private TraitType<String> colNameType;
    private TraitType<Long> periodicityType;
    private RelationTypeIF containsRelType;
    private static final Logger LOG = LoggerFactory.getLogger(DataResource.class);

    /**
     * @throws EntityException
     */
    public void entityUseCaseForPaasCreation() throws EntityException {
        EntityContextIF paasECtx = Factory.createPaasEntityContext();
        createTypes(paasECtx);
        EntityIF subsDB = createDBEntity(paasECtx,"subscriberdb",  "cassandra:ec2-west");
        EntityIF subsTable = createTableEntity(paasECtx,"subscribertable",  "cassandra","subscribercluster:subscriberks:subscribercf");
        EntityIF subsKs = createKeyspaceEntity(paasECtx,"subscriberks",  3, "SimpleStrategy");
        EntityIF subsCf = createColumnFamilyEntity(paasECtx, "subscribercf", CASSANDRA_CF_TYPE,"String", "String",new String[] {"c1","c2"}, new String[]{"String", "String"});
        RelationIF rel = paasECtx.createRelation("subscriber_ks_cf",containsRelType);
        rel.addPlayers(keySpaceType, subsKs);
        rel.addPlayers(columnFamilyType, subsCf);
        RelationIF reldb = paasECtx.createRelation("subscriber_db_table",containsRelType);
        reldb.addPlayers(dbType, subsDB);
        reldb.addPlayers(tableType, subsTable);
        RelationIF relTable = paasECtx.createRelation("subscriber_table_ks",containsRelType);
        relTable.addPlayers(tableType, subsTable);
        relTable.addPlayers(keySpaceType, subsKs);
    }
    /**
     * @throws EntityException
     */
    public void entityUseCaseForTimeSeries() throws EntityException {
        EntityContextIF paasECtx = Factory.createPaasEntityContext();
        createTypes(paasECtx);
        EntityIF subsDB = createDBEntity(paasECtx,"subscriberdb",  "cassandra:ec2-west");
        EntityIF subsTable = createTableEntity(paasECtx,"subscribertable",  "cassandra","subscribercluster:subscriberks:subscribercf");
        EntityIF subsKs = createKeyspaceEntity(paasECtx,"subscriberks",  3, "SimpleStrategy");
        EntityIF subsCf = createTimeSeriesEntity(paasECtx, "subscriberts", CASSANDRA_TIMESERIES_TYPE,86400L);
        RelationIF rel = paasECtx.createRelation("subscriber_ks_cf",containsRelType);
        rel.addPlayers(keySpaceType, subsKs);
        rel.addPlayers(columnFamilyType, subsCf);
        RelationIF reldb = paasECtx.createRelation("subscriber_db_table",containsRelType);
        reldb.addPlayers(dbType, subsDB);
        reldb.addPlayers(tableType, subsTable);
        RelationIF relTable = paasECtx.createRelation("subscriber_table_ks",containsRelType);
        relTable.addPlayers(tableType, subsTable);
        relTable.addPlayers(keySpaceType, subsKs);
    }
    /**
     * @param paasECtx
     * @param name
     * @param defaultValidation
     * @param periodicity
     * @return
     * @throws EntityException
     */
    public EntityIF createTimeSeriesEntity(EntityContextIF paasECtx, String name, String defaultValidation, Long periodicity) throws EntityException {
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(keySpaceType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, name,
                PredicateCompare.EQUAL));
        final Collection<EntityId> EntityIds = paasECtx.getEntityIds(predicate);
        if (EntityIds.size() > 0)
            throw new EntityException("cf with  name\"" + name + "\" already exist");
        EntityIF subsTimeSeries = paasECtx.createEntity(timeSeriesType);
        Trait<TraitType<String>, String> cfName = paasECtx.createTrait(nameType, name);
        Trait<TraitType<String>, String> comp = paasECtx.createTrait(comparatorType, "Long");
        Trait<TraitType<String>, String> keyVal = paasECtx.createTrait(keyValidationType, "Long");
        Trait<TraitType<String>, String> defVal = paasECtx.createTrait(defaultValidationType, defaultValidation);
        Trait<TraitType<Long>, Long> pdcity = paasECtx.createTrait(periodicityType, periodicity);
        subsTimeSeries.addTrait(cfName);
        subsTimeSeries.addTrait(comp);
        subsTimeSeries.addTrait(keyVal);
        subsTimeSeries.addTrait(defVal);
        subsTimeSeries.addTrait(pdcity);
        return subsTimeSeries;
    }
    
    /**
     * @throws EntityException
     */
    public void entityUseCaseForPaasQueryRoot() throws EntityException {
        String dbName = "subddb";
        EntityContextIF paasECtx = Factory.createPaasEntityContext();
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(dbType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, dbName,
                PredicateCompare.EQUAL));
        final Collection<EntityIF> Entity = paasECtx.getPaasEntities(predicate);
        if (Entity.size() > 0) {
            EntityIF dbentity =  (EntityIF) Entity.toArray()[0];//db entity
            Map<EntityTypeIF, Collection<EntityIF>> entMap = paasECtx.getEntitiesSubTree(dbentity);
            for (Entry<EntityTypeIF, Collection<EntityIF>> entry: entMap.entrySet()) {
                LOG.info("entyr type: "+entry.getKey().getName());
                for (EntityIF entity: entry.getValue()) {
                    LOG.info("entity name is " + entity.getName());
                }
            }
        }      
    }
    
    /**
     * @throws EntityException
     */
    public void entityUseCaseForPaasQueryRec() throws EntityException {
        String dbName = "subddb";
        EntityContextIF paasECtx = Factory.createPaasEntityContext();
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(dbType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, dbName,
                PredicateCompare.EQUAL));
        final Collection<EntityIF> Entity = paasECtx.getPaasEntities(predicate);
        if (Entity.size() > 0) {
            EntityIF dbentity =  (EntityIF) Entity.toArray()[0];//db entity
            LOG.info("db name is "+dbentity.getName());
            Collection<RelationIF> containsRelations = (Collection<RelationIF>)dbentity.getRelations(containsRelType, 1);//get all relations
            if (containsRelations.size() == 1) {
                RelationIF rel = (RelationIF)containsRelations.toArray()[0];
                Collection<EntityIF> tables = rel.getPlayers(tableType, "sustable");
                if (tables.size() == 1) {
                    EntityIF table = (EntityIF)tables.toArray()[0];
                    LOG.info("table name is "+table.getName());
                    Collection<RelationIF> relTable = (Collection<RelationIF>)table.getRelations(containsRelType, 1);//get all relations
                    if (containsRelations.size() == 1) {
                        RelationIF reltbl = (RelationIF)relTable.toArray()[0];
                        Collection<EntityIF> kspcs = reltbl.getPlayers(keySpaceType);
                        if (kspcs.size() == 1) {
                            EntityIF ks = (EntityIF)tables.toArray()[0];
                            LOG.info("keyspace name is "+ks.getName());
                        }
                    }

                }
            }            
        }
    }
    
    /**
     * @param paasECtx
     * @param name
     * @param resolution
     * @return
     * @throws EntityException
     */
    public EntityIF createDBEntity(EntityContextIF paasECtx, String name,   String resolution) throws EntityException {
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(dbType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, name,
                PredicateCompare.EQUAL));
        final Collection<EntityId> EntityIds = paasECtx.getEntityIds(predicate);
        if (EntityIds.size() > 0)
            throw new EntityException("db with  name\"" + name + "\" already exist");
        EntityIF subsDB = paasECtx.createEntity(dbType);
        Trait<TraitType<String>, String> dbName = paasECtx.createTrait(nameType, name);
        subsDB.addTrait(dbName);
        //subsTable.addPaasTrait(paasECtx.createTrait(storageType, storagetype));//may or may not be needed
        subsDB.addTrait(paasECtx.createTrait(resolutionType, resolution));//may not be used
        return subsDB;
    }
    /**
     * @param paasECtx
     * @param name
     * @param storagetype
     * @param resolution
     * @return
     * @throws EntityException
     */
    public EntityIF createTableEntity(EntityContextIF paasECtx, String name, String storagetype,  String resolution) throws EntityException {
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(tableType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, name,
                PredicateCompare.EQUAL));
        final Collection<EntityId> EntityIds = paasECtx.getEntityIds(predicate);
        if (EntityIds.size() > 0)
            throw new EntityException("table with  name\"" + name + "\" already exist");
        EntityIF subsTable = paasECtx.createEntity(tableType);
        Trait<TraitType<String>, String> tableName = paasECtx.createTrait(nameType, name);
        subsTable.addTrait(tableName);
        subsTable.addTrait(paasECtx.createTrait(storageType, storagetype));
        subsTable.addTrait(paasECtx.createTrait(resolutionType, resolution));
        return subsTable;
    }
    /**
     * @param paasECtx
     * @param name
     * @param rf
     * @param strategyname
     * @return
     * @throws EntityException
     */
    public EntityIF createKeyspaceEntity(EntityContextIF paasECtx, String name,  Integer rf, String strategyname) throws EntityException {
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(keySpaceType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, name,
                PredicateCompare.EQUAL));
        final Collection<EntityId> EntityIds = paasECtx.getEntityIds(predicate);
        if (EntityIds.size() > 0)
            throw new EntityException("keyspace with  name\"" + name + "\" already exist");
        EntityIF subsKs = paasECtx.createEntity(keySpaceType);
        Trait<TraitType<String>, String> ksName = paasECtx.createTrait(nameType, name);
        Trait<TraitType<Integer>, Integer> replicationFactor = paasECtx.createTrait(replicationType, rf);;
        Trait<TraitType<String>, String> strategy = paasECtx.createTrait(nameType, name);
        subsKs.addTrait(ksName);
        subsKs.addTrait(replicationFactor);
        subsKs.addTrait(strategy);
        return subsKs;
    }
    /**
     * @param paasECtx
     * @param name
     * @param comparator
     * @param keyValidation
     * @param defaultValidation
     * @param columnNames
     * @param types
     * @return
     * @throws EntityException
     */
    public EntityIF createColumnFamilyEntity(EntityContextIF paasECtx, String name, String comparator, String keyValidation, String defaultValidation, String [] columnNames, String[] types) throws EntityException {
        final AllEntityPredicate predicate = new AllEntityPredicate(new EntityTypePredicate(keySpaceType,
                PredicateCompare.EQUAL), new TraitPredicate<String>(nameType, name,
                PredicateCompare.EQUAL));
        final Collection<EntityId> EntityIds = paasECtx.getEntityIds(predicate);
        if (EntityIds.size() > 0)
            throw new EntityException("cf with  name\"" + name + "\" already exist");
        EntityIF subsCf = paasECtx.createEntity(columnFamilyType);
        Trait<TraitType<String>, String> cfName = paasECtx.createTrait(nameType, name);
        Trait<TraitType<String>, String> comp = paasECtx.createTrait(comparatorType, comparator);
        Trait<TraitType<String>, String> keyVal = paasECtx.createTrait(keyValidationType, keyValidation);
        Trait<TraitType<String>, String> defVal = paasECtx.createTrait(defaultValidationType, defaultValidation);
        for (String colName: columnNames) {
            subsCf.addTrait(paasECtx.createTrait(colNameType, colName));
        }
        for (String type: types) {
            subsCf.addTrait(paasECtx.createTrait(columnValidationType, type));
        }

        subsCf.addTrait(cfName);
        subsCf.addTrait(comp);
        subsCf.addTrait(keyVal);
        subsCf.addTrait(defVal);
        return subsCf;
    }
    /**
     * @param ctx
     * @throws EntityException
     */
    public void createTypes(final EntityContextIF ctx) throws EntityException {
       keySpaceType = getOrCreateEntityType(ctx, CASSANDRA_KEYSPACE_ENTITY_TYPE);
       columnFamilyType = getOrCreateEntityType(ctx, CASSANDRA_CF_TYPE); 
       tableType = getOrCreateEntityType(ctx, PAAS_TABLE_ENTITY_TYPE);
       clusterType = getOrCreateEntityType(ctx, PAAS_CLUSTER_ENTITY_TYPE);
       dbType = getOrCreateEntityType(ctx, PAAS_DB_ENTITY_TYPE); 
       nameType =  getOrCreateTraitType(ctx, NAME_TYPE, String.class);
       replicationType = getOrCreateTraitType(ctx, RF_TYPE, Integer.class);
       strategyType = getOrCreateTraitType(ctx, STRATEGY_TYPE, String.class);
       comparatorType = getOrCreateTraitType(ctx, COMPARATOR_TYPE, String.class);
       keyValidationType = getOrCreateTraitType(ctx, KEY_VALIDATION_CLASS_TYPE, String.class); 
       columnValidationType = getOrCreateTraitType(ctx, COLUMN_VALIDATION_CLASS_TYPE, String.class); 
       defaultValidationType = getOrCreateTraitType(ctx, DEFAULT_VALIDATION_CLASS_TYPE, String.class); 
       colNameType = getOrCreateTraitType(ctx, COLUMN_NAME_TYPE, String.class);
       storageType = getOrCreateTraitType(ctx, STORAGE_TYPE, String.class);
       resolutionType = getOrCreateTraitType(ctx, RESOLUTION_TYPE, String.class);
       containsRelType = getOrCreateRelationType(ctx, CONTAINS_TYPE);
    }
       
    /**
     * @param ctx
     * @param name
     * @param traitType
     * @return
     */
    private <T> TraitType<T> getOrCreateTraitType(final EntityContextIF ctx, final String name, final Class<T> traitType) {
        TraitType<T> type = ctx.getTraitType(name);
        if (type == null)
            type = ctx.createTraitType(name, traitType);
        return type;
    }
    /**
     * @param ctx
     * @param typeName
     * @return
     * @throws EntityException
     */
    private RelationTypeIF getOrCreateRelationType(final EntityContextIF ctx, final String typeName) throws EntityException
   {
        RelationTypeIF type = ctx.getRelationType(typeName);
        if (type == null)
            type = ctx.createRelationType(typeName);
        return type;
    }

//    private PaasRoleType getOrCreateRoleType(final PaasEntityContext ctx, final String typeName) throws PaasEntityException
//    {
//        PaasRoleType type = ctx.getRoleType(typeName);
//        if (type == null)
//            type = ctx.createRoleType(typeName);
//        return type;
//    }
   /**
 * @param ctx
 * @param typeName
 * @return
 * @throws EntityException
 */
public EntityTypeIF getOrCreateEntityType(final EntityContextIF ctx, final String typeName) throws EntityException
     {
        EntityTypeIF type = ctx.getEntityType(typeName);
        if (type == null)
            type = ctx.createEntityType(typeName);
        return type;
    }
}
