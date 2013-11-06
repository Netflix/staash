package com.netflix.paas.meta;

import java.util.Collection;
import java.util.Iterator;


/**
 * @author ssingh
 *
 */
public interface RelationIF {
   
    /**
     * @param role
     * @param subjects
     */
//    public void addPlayers(PaasRoleType role, PaasEntity... subjects);
    /**
     * @param role
     * @param subjects
     */
    public void addPlayers(EntityTypeIF role, EntityIF... subjects);    
    /**
     * @return
     */
    public RelationId getId();

    
    /**
     * @param max
     * @return
     */
    public Collection<EntityIF> getPlayers() ;

   
    /**
     * @param roleType
     * @param max
     * @return
     */
//    public Collection<PaasEntity> getPlayers(PaasRoleType roleType) ;
    
    /**
     * @param entityType
     * @param max
     * @return
     */
    public Collection<EntityIF> getPlayers(EntityTypeIF entityType) ;
    
    /**
     * @param entityType
     * @param name
     * @return
     */
    public Collection<EntityIF> getPlayers(EntityTypeIF entityType, String name) ;



    
    /**
     * @param max
     * @return
     */
//    public Collection<PaasRoleType> getRoles() ;

   
    /**
     * @param player
     * @param max
     * @return
     */
//    public Collection<PaasRoleType> getRoles(PaasEntity player) ;

   
    /**
     * @return
     */
    public RelationTypeIF getType() ;

   
    /**
     * @return
     */
    public Iterator<EntityIF> iteratorForPlayers() ;

   
    /**
     * @param roleType
     * @return
     */
//    public Iterator<PaasEntity> iteratorForPlayers(PaasRoleType roleType) ;
    
  public Iterator<EntityIF> iteratorForPlayers(EntityTypeIF roleType) ;


    
    /**
     * @return
     */
//    public Iterator<PaasRoleType> iteratorForRoles() ;

    
    /**
     * @param player
     * @return
     */
//    public Iterator<PaasRoleType> iteratorForRoles(PaasEntity player) ;

   
    /**
     * @param role
     */
//    public void remove(PaasRoleType role) ;
  
public void remove(EntityTypeIF role) ;
  
public void remove(EntityTypeIF role, EntityIF... players) ;


   
    /**
     * @param role
     * @param players
     */
//    public void remove(PaasRoleType role, PaasEntity... players) ;

    
    /**
     * @param player
     */
    public void remove(EntityIF player) ;

}
