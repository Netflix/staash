
package com.netflix.staash.test.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequiresColumnFamily {
  String ksName();
  String cfName();
  boolean isCounter() default false;
  String comparator() default "UTF8Type";
  String defaultValidator() default "UTF8Type";
  String keyValidator() default "UTF8Type";

  boolean truncateExisting() default false;
}
