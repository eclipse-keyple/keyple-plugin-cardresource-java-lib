/* **************************************************************************************
 * Copyright (c) 2023 Calypso Networks Association https://calypsonet.org/
 *
 * See the NOTICE file(s) distributed with this work for additional information
 * regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ************************************************************************************** */
package org.eclipse.keyple.plugin.cardresource;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import org.eclipse.keyple.core.service.PoolPlugin;
import org.eclipse.keyple.core.service.SmartCardServiceProvider;
import org.junit.Before;
import org.junit.Test;

public class CardResourcePluginFactoryBuilderTest {

  private static final String PLUGIN_NAME = "CardResourcePlugin";

  @Before
  public void setUp() {}

  @Test
  public void build_whenPluginNameAndProfileNamesAreSet_thenPluginContainsNameAndProfileNames() {
    Collection<String> profileNames = Arrays.asList("profile1", "profile2");
    CardResourcePluginFactoryBuilder.Builder builder =
        CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, profileNames);
    Set<String> expectedCardResourceProfileNames = new HashSet<String>(profileNames);
    PoolPlugin poolPlugin =
        (PoolPlugin) SmartCardServiceProvider.getService().registerPlugin(builder.build());
    assertThat(poolPlugin.getName()).isEqualTo(PLUGIN_NAME);
    assertThat(poolPlugin.getReaderGroupReferences()).isEqualTo(expectedCardResourceProfileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void build_whenNullPluginName_thenThrowIAE() {
    Collection<String> profileNames = Arrays.asList("profile1");
    CardResourcePluginFactoryBuilder.builder(null, profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void build_whenEmptyPluginName_thenThrowIAE() {
    Collection<String> profileNames = Arrays.asList("profile1");
    CardResourcePluginFactoryBuilder.builder("", profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenEmptyProfileNames_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, Collections.<String>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenNullProfileNames_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenNullProfileName_thenThrowIAE() {
    Collection<String> profileNames = Arrays.asList("profile1", null, "profile2");
    CardResourcePluginFactoryBuilder.Builder builder =
        CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, profileNames);
  }
}
