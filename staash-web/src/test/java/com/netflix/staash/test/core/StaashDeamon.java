
package com.netflix.staash.test.core;

import org.apache.cassandra.service.CassandraDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StaashDeamon extends CassandraDaemon {

  private static final Logger logger = LoggerFactory.getLogger(StaashDeamon.class);


  private static final StaashDeamon instance = new StaashDeamon();

	public static void main(String[] args) {
		System.setProperty("cassandra-foreground", "true");
		System.setProperty("log4j.defaultInitOverride", "true");
		System.setProperty("log4j.configuration", "log4j.properties");
		instance.activate();
	}

  @Override
  protected void setup() {
    super.setup();
  }

  @Override
  public void init(String[] arguments) throws IOException {
    super.init(arguments);
  }

  @Override
  public void start() {
    super.start();
  }

  @Override
  public void stop() {
    super.stop();
  }
}