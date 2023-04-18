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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.keyple.card.generic.GenericCardSelection;
import org.eclipse.keyple.card.generic.GenericExtensionService;
import org.eclipse.keyple.core.plugin.PluginIOException;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.SmartCardServiceProvider;
import org.eclipse.keyple.core.service.resource.*;
import org.eclipse.keyple.core.service.resource.spi.CardResourceProfileExtension;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CardResourcePluginAdapterTest {

  private static final String CARD_RESOURCE_PROFILE_NAME_1 = "profile1";
  private static final String CARD_RESOURCE_PROFILE_NAME_2 = "profile2";
  private static final String CARD_RESOURCE_PROFILE_NAME_3 = "profile3";
  private final Set<String> cardResourceProfileNames =
      new HashSet<String>(
          Arrays.asList(CARD_RESOURCE_PROFILE_NAME_1, CARD_RESOURCE_PROFILE_NAME_2));
  private final StubUtil stubUtil = new StubUtil();
  private Plugin plugin;
  private CardResourceService cardResourceService;
  private CardResourcePluginAdapter adapter;

  @Before
  public void setUp() throws InterruptedException {

    stubUtil.initPluginAndReader();
    plugin = stubUtil.getPlugin();

    GenericCardSelection cardSelection1 =
        GenericExtensionService.getInstance()
            .createCardSelection()
            .filterByPowerOnData(StubUtil.POWER_ON_DATA);

    CardResourceProfileExtension cardResourceExtension1 =
        GenericExtensionService.getInstance().createCardResourceProfileExtension(cardSelection1);

    GenericCardSelection cardSelection2 =
        GenericExtensionService.getInstance()
            .createCardSelection()
            .filterByPowerOnData(StubUtil.POWER_ON_DATA);

    CardResourceProfileExtension cardResourceExtension2 =
        GenericExtensionService.getInstance().createCardResourceProfileExtension(cardSelection2);

    cardResourceService = CardResourceServiceProvider.getService();
    cardResourceService
        .getConfigurator()
        .withPlugins(
            PluginsConfigurator.builder()
                .addPlugin(plugin, new StubUtil.ReaderConfigurator())
                .build())
        .withCardResourceProfiles(
            CardResourceProfileConfigurator.builder(
                    CARD_RESOURCE_PROFILE_NAME_1, cardResourceExtension1)
                .withReaderNameRegex(StubUtil.CONTACT_READER_NAME)
                .build(),
            CardResourceProfileConfigurator.builder(
                    CARD_RESOURCE_PROFILE_NAME_2, cardResourceExtension2)
                .withReaderNameRegex(StubUtil.CONTACTLESS_READER_NAME)
                .build())
        .configure();
    cardResourceService.start();

    adapter = new CardResourcePluginAdapter(cardResourceProfileNames);
  }

  @After
  public void tearDown() {
    cardResourceService.stop();
    SmartCardServiceProvider.getService().unregisterPlugin(plugin.getName());
  }

  @Test
  public void GetName_shouldReturnPluginName() {
    assertThat(adapter.getName()).isEqualTo(CardResourcePluginFactoryAdapter.PLUGIN_NAME);
  }

  @Test
  public void GetReaderGroupReferences_shouldReturnRegisteredReferences() {
    SortedSet<String> expectedReferences =
        new TreeSet<String>(
            Arrays.asList(CARD_RESOURCE_PROFILE_NAME_1, CARD_RESOURCE_PROFILE_NAME_2));
    assertThat(adapter.getReaderGroupReferences()).isEqualTo(expectedReferences);
  }

  @Test
  public void AllocateReader_whenReaderExists_shouldReturnReader() throws PluginIOException {
    ReaderSpi allocatedReader = adapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    assertThat(allocatedReader).isInstanceOf(CardResourceReaderAdapter.class);
    assertThat(allocatedReader.getName())
        .isEqualTo("CARD_RESOURCE_" + StubUtil.CONTACT_READER_NAME);
  }

  @Test(expected = PluginIOException.class)
  public void AllocateReader_whenReaderDoesntExist_shouldThrowPluginIOException()
      throws PluginIOException {
    adapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_3);
  }

  @Test
  public void ReleaseReader_whenReaderExists_shouldNullifyCardResource() throws PluginIOException {
    ReaderSpi allocatedReader = adapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    assertThat(((CardResourceReaderAdapter) allocatedReader).getCardResource()).isNotNull();
    adapter.releaseReader(allocatedReader);
    assertThat(((CardResourceReaderAdapter) allocatedReader).getCardResource()).isNull();
  }

  @Test
  public void ReleaseReader_whenReaderDoesntExist_shouldNotThrowException()
      throws PluginIOException {
    ReaderSpi allocatedReader = adapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    assertThat(((CardResourceReaderAdapter) allocatedReader).getCardResource()).isNotNull();
    adapter.releaseReader(allocatedReader);
    assertThat(((CardResourceReaderAdapter) allocatedReader).getCardResource()).isNull();
    adapter.releaseReader(allocatedReader);
  }

  @Test
  public void testOnUnregister() {
    adapter.onUnregister(); // Method does nothing, nothing to assert
  }
}
