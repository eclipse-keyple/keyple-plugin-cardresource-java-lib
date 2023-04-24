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
import static org.mockito.Mockito.mock;

import java.util.*;
import org.eclipse.keyple.core.plugin.PluginIOException;
import org.eclipse.keyple.core.plugin.spi.PoolPluginSpi;
import org.eclipse.keyple.core.service.resource.CardResourceService;
import org.junit.Before;
import org.junit.Test;

public class CardResourcePluginFactoryBuilderTest {

  private static final String PLUGIN_NAME = "CardResourcePlugin";
  private CardResourceService cardResourceService;

  @Before
  public void setUp() {
    cardResourceService = mock(CardResourceService.class);
  }

  @Test
  public void build_whenPluginNameAndProfileNamesAreSet_thenPluginContainsNameAndProfileNames()
      throws PluginIOException {
    String profileName = "profile1";
    CardResourcePluginFactoryBuilder.Builder builder =
        CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, cardResourceService, profileName);
    CardResourcePluginFactoryAdapter factory = (CardResourcePluginFactoryAdapter) builder.build();
    PoolPluginSpi poolPlugin = factory.getPoolPlugin();
    assertThat(poolPlugin.getName()).isEqualTo(PLUGIN_NAME);
    assertThat(poolPlugin.getReaderGroupReferences()).containsExactly(profileName);
  }

  @Test
  public void
      build_whenPluginNameAndProfileNamesAsCollectionAreSet_thenPluginContainsNameAndProfileNames()
          throws PluginIOException {
    List<String> profileNames = Arrays.asList("profile1", "profile2");
    CardResourcePluginFactoryBuilder.Builder builder =
        CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, cardResourceService, profileNames);
    CardResourcePluginFactoryAdapter factory = (CardResourcePluginFactoryAdapter) builder.build();
    PoolPluginSpi poolPlugin = factory.getPoolPlugin();
    assertThat(poolPlugin.getName()).isEqualTo(PLUGIN_NAME);
    assertThat(poolPlugin.getReaderGroupReferences())
        .containsExactlyInAnyOrderElementsOf(profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void build_whenNullPluginName_thenThrowIAE() {
    Collection<String> profileNames = Collections.singletonList("profile1");
    CardResourcePluginFactoryBuilder.builder(null, cardResourceService, profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void build_whenNullCardResourceService_thenThrowIAE() {
    Collection<String> profileNames = Collections.singletonList("profile1");
    CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, null, profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void build_whenEmptyPluginName_thenThrowIAE() {
    Collection<String> profileNames = Collections.singletonList("profile1");
    CardResourcePluginFactoryBuilder.builder("", cardResourceService, profileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenEmptyProfileNames_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.builder(
        PLUGIN_NAME, cardResourceService, Collections.<String>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenNullProfileNames_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.builder(
        PLUGIN_NAME, cardResourceService, (Collection<String>) null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenNullProfileName_thenThrowIAE() {
    Collection<String> profileNames = Arrays.asList("profile1", null, "profile2");
    CardResourcePluginFactoryBuilder.builder(PLUGIN_NAME, cardResourceService, profileNames);
  }
}
