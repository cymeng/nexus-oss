/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.nexus.plugins.ui;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exposes build number/version as filtered by build.
 */
@Named
@Singleton
public class BuildNumberService
{
  private static final String RESOURCE_NAME = "version.properties";

  private static final Logger log = LoggerFactory.getLogger(BuildNumberService.class);

  private final String buildNumber;

  public BuildNumberService() {
    this.buildNumber = loadVersion();
    log.debug("Build number: {}", buildNumber);
  }

  private String loadVersion() {
    URL url = getClass().getResource(RESOURCE_NAME);
    if (url == null) {
      log.error("Missing resource: {}", "version.properties");
      return "unknown-version";
    }

    Properties props = new Properties();
    InputStream input = null;
    try {
      input = url.openStream();
      props.load(input);

      log.debug("Loaded properties: {}", props);
    }
    catch (Exception e) {
      log.warn("Could not determine build number", e);
    }
    finally {
      IOUtils.closeQuietly(input);
    }

    return props.getProperty("version", "unknown-version");
  }

  public String getBuildNumber() {
    return buildNumber;
  }

}
