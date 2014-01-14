
package com.netflix.staash.test.core;

import org.apache.cassandra.locator.AbstractReplicationStrategy;
import org.apache.cassandra.locator.SimpleStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresKeyspace {
  String ksName();

  int replication() default 1;

  Class<? extends AbstractReplicationStrategy> strategy() default SimpleStrategy.class;

  String strategyOptions() default "";

}
