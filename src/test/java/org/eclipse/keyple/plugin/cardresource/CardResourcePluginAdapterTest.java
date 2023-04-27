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
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.calypsonet.terminal.reader.CardReader;
import org.calypsonet.terminal.reader.selection.spi.SmartCard;
import org.eclipse.keyple.core.common.KeypleReaderExtension;
import org.eclipse.keyple.core.plugin.PluginIOException;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.resource.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CardResourcePluginAdapterTest {

  private static final String CONTACT_READER_NAME = "Reader1";

  interface ReaderExtension extends KeypleReaderExtension, ReaderSpi {}

  private static final String PLUGIN_NAME = "CardResourcePlugin";
  private static final String CARD_RESOURCE_PROFILE_NAME_1 = "profile1";
  private static final String CARD_RESOURCE_PROFILE_NAME_2 = "profile2";
  private static final String CARD_RESOURCE_PROFILE_NAME_3 = "profile3";
  private static final String CARD_RESOURCE_PROFILE_NAME_4 = "profile4";
  private static final String CARD_RESOURCE_PROFILE_NAME_5 = "profile5";
  private final Set<String> cardResourceProfileNames =
      new HashSet<String>(
          Arrays.asList(CARD_RESOURCE_PROFILE_NAME_1, CARD_RESOURCE_PROFILE_NAME_2));
  private CardResourceService cardResourceService;
  private CardResource cardResource;
  private CardResourcePluginAdapter pluginAdapter;

  @Before
  public void setUp() {
    CardReader reader = mock(CardReader.class);
    SmartCard smartCard = mock(SmartCard.class);

    ReaderExtension readerExtension = mock(ReaderExtension.class);
    when(readerExtension.getName()).thenReturn(CONTACT_READER_NAME);

    cardResource = mock(CardResource.class);
    when(cardResource.getReader()).thenReturn(reader);
    when(cardResource.getReaderExtension()).thenReturn(readerExtension);
    when(cardResource.getSmartCard()).thenReturn(smartCard);

    cardResourceService = mock(CardResourceService.class);
    when(cardResourceService.getCardResource(CARD_RESOURCE_PROFILE_NAME_1))
        .thenReturn(cardResource);
    when(cardResourceService.getCardResource(CARD_RESOURCE_PROFILE_NAME_3))
        .thenThrow(IllegalArgumentException.class);
    when(cardResourceService.getCardResource(CARD_RESOURCE_PROFILE_NAME_4))
        .thenThrow(IllegalStateException.class);
    when(cardResourceService.getCardResource(CARD_RESOURCE_PROFILE_NAME_5)).thenReturn(null);
    pluginAdapter =
        new CardResourcePluginAdapter(PLUGIN_NAME, cardResourceService, cardResourceProfileNames);
  }

  @After
  public void tearDown() {
    cardResourceService.stop();
  }

  @Test
  public void GetName_shouldReturnPluginName() {
    assertThat(pluginAdapter.getName()).isEqualTo(PLUGIN_NAME);
  }

  @Test
  public void GetReaderGroupReferences_shouldReturnRegisteredReferences() {
    SortedSet<String> expectedProfileNames =
        new TreeSet<String>(
            Arrays.asList(CARD_RESOURCE_PROFILE_NAME_1, CARD_RESOURCE_PROFILE_NAME_2));
    assertThat(pluginAdapter.getReaderGroupReferences()).isEqualTo(expectedProfileNames);
  }

  @Test
  public void AllocateReader_whenReaderExists_shouldReturnReader() throws Exception {
    when(cardResourceService.getCardResource(CARD_RESOURCE_PROFILE_NAME_1))
        .thenReturn(cardResource);
    ReaderSpi allocatedReader = pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    verify(cardResourceService).getCardResource(CARD_RESOURCE_PROFILE_NAME_1);
    verifyNoMoreInteractions(cardResourceService);
    assertThat(allocatedReader).isInstanceOf(CardResourceReaderAdapter.class);
    assertThat(allocatedReader.getName()).isEqualTo(CONTACT_READER_NAME + " (CardResource)");
  }

  @Test(expected = PluginIOException.class)
  public void AllocateReader_whenProfileIsNotConfigured_shouldThrowPluginIOException()
      throws PluginIOException {
    pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_3);
  }

  @Test(expected = PluginIOException.class)
  public void AllocateReader_whenServiceIsNotStarted_shouldThrowPluginIOException()
      throws PluginIOException {
    pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_4);
  }

  @Test(expected = PluginIOException.class)
  public void AllocateReader_whenNoCardResourceIsAvailable_shouldThrowPluginIOException()
      throws PluginIOException {
    pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_5);
  }

  @Test
  public void ReleaseReader_whenReaderExists_shouldNullifyCardResource() throws Exception {
    ReaderSpi allocatedReader = pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    cardResource = ((CardResourceReaderAdapter) allocatedReader).getCardResource();
    pluginAdapter.releaseReader(allocatedReader);
    verify(cardResourceService).getCardResource(CARD_RESOURCE_PROFILE_NAME_1);
    verify(cardResourceService).releaseCardResource(cardResource);
    verifyNoMoreInteractions(cardResourceService);
  }

  @Test
  public void OnUnregister_shouldNotInteractWithCardResourceService() throws Exception {
    ReaderSpi allocatedReader = pluginAdapter.allocateReader(CARD_RESOURCE_PROFILE_NAME_1);
    assertThat(((CardResourceReaderAdapter) allocatedReader).getCardResource()).isNotNull();
    pluginAdapter.onUnregister();
    verify(cardResourceService).getCardResource(CARD_RESOURCE_PROFILE_NAME_1);
    verifyNoMoreInteractions(cardResourceService);
  }
}
