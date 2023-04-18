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

import static org.assertj.core.api.Assertions.assertThat;

import org.calypsonet.terminal.reader.CardReader;
import org.calypsonet.terminal.reader.selection.CardSelectionManager;
import org.calypsonet.terminal.reader.selection.CardSelectionResult;
import org.calypsonet.terminal.reader.selection.spi.CardSelection;
import org.calypsonet.terminal.reader.selection.spi.SmartCard;
import org.eclipse.keyple.card.generic.GenericExtensionService;
import org.eclipse.keyple.core.plugin.CardIOException;
import org.eclipse.keyple.core.plugin.ReaderIOException;
import org.eclipse.keyple.core.service.*;
import org.eclipse.keyple.core.service.resource.CardResource;
import org.eclipse.keyple.core.util.HexUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CardResourceReaderAdapterTest {

  private final StubUtil stubUtil = new StubUtil();
  private Plugin plugin;
  private CardResource cardResource;
  private CardResourceReaderAdapter adapter;
  private SmartCard smartcard;

  @Before
  public void setUp() throws InterruptedException {
    stubUtil.initPluginAndReader();
    plugin = stubUtil.getPlugin();
    CardReader reader = stubUtil.getCardReader();

    CardSelectionManager cardSelectionManager =
        SmartCardServiceProvider.getService().createCardSelectionManager();
    CardSelection cardSelection =
        GenericExtensionService.getInstance().createCardSelection().filterByDfName(StubUtil.AID);
    cardSelectionManager.prepareSelection(cardSelection);
    CardSelectionResult selectionResult = cardSelectionManager.processCardSelectionScenario(reader);
    smartcard = selectionResult.getActiveSmartCard();

    cardResource = new CardResource(reader, smartcard);
    adapter = new CardResourceReaderAdapter(cardResource);
  }

  @After
  public void tearDown() {
    SmartCardService smartCardService = SmartCardServiceProvider.getService();
    smartCardService.unregisterPlugin(plugin.getName());
  }

  @Test
  public void getName_shouldReturnFormattedName() {
    String expectedName = "CARD_RESOURCE_" + cardResource.getReader().getName();
    assertThat(adapter.getName()).isEqualTo(expectedName);
  }

  @Test
  public void getCardResource_shouldReturnCardResource() {
    assertThat(adapter.getCardResource()).isEqualTo(cardResource);
  }

  @Test
  public void isPhysicalChannelOpen_shouldReturnTrue() {
    assertThat(adapter.isPhysicalChannelOpen()).isTrue();
  }

  @Test
  public void checkCardPresence_shouldReturnTrue() {
    assertThat(adapter.checkCardPresence()).isTrue();
  }

  @Test
  public void getPowerOnData_shouldReturnCardPowerOnData() {
    String expectedPowerOnData = cardResource.getSmartCard().getPowerOnData();
    assertThat(adapter.getPowerOnData()).isEqualTo(expectedPowerOnData);
  }

  @Test
  public void isContactless_whenReaderIsContactless_shouldReturnTrue() {
    CardReader contactReader = plugin.getReader(StubUtil.CONTACTLESS_READER_NAME);
    CardResource contactCardResource = new CardResource(contactReader, cardResource.getSmartCard());
    CardResourceReaderAdapter contactAdapter = new CardResourceReaderAdapter(contactCardResource);
    assertThat(contactAdapter.isContactless())
        .isEqualTo(contactCardResource.getReader().isContactless());
    assertThat(contactAdapter.isContactless()).isTrue();
  }

  @Test
  public void isContactless_whenReaderIsContact_shouldReturnFalse() {
    // default cardResource contains a contact reader type
    assertThat(adapter.isContactless()).isEqualTo(cardResource.getReader().isContactless());
    assertThat(adapter.isContactless()).isFalse();
  }

  @Test
  public void transmitApdu_whenCardIsResponding_shouldReturnApduR()
      throws ReaderIOException, CardIOException {
    assertThat(adapter.transmitApdu(HexUtil.toByteArray(StubUtil.APDU_C)))
        .isEqualTo(HexUtil.toByteArray(StubUtil.APDU_R));
  }

  @Test(expected = CardIOException.class)
  public void transmitApdu_whenCardIsNotResponding_shouldThrowCardIOException()
      throws ReaderIOException, CardIOException {
    assertThat(adapter.transmitApdu(HexUtil.toByteArray(StubUtil.UNKNOWN_APDU_C)))
        .isEqualTo(HexUtil.toByteArray(StubUtil.APDU_R));
  }

  @Test
  public void getSelectedSmartCard_shouldReturnSmartcard() {
    assertThat(adapter.getSelectedSmartCard()).isEqualTo(smartcard);
  }
}
