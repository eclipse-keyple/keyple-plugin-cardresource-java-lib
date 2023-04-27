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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.calypsonet.terminal.reader.CardReader;
import org.calypsonet.terminal.reader.selection.spi.SmartCard;
import org.eclipse.keyple.core.common.KeypleReaderExtension;
import org.eclipse.keyple.core.plugin.CardIOException;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.resource.CardResource;
import org.eclipse.keyple.core.util.HexUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class CardResourceReaderAdapterTest {
  private static final String CONTACT_READER_NAME = "ContactReader";
  private static final String CONTACTLESS_READER_NAME = "ContactlessReader";
  static final String APDU_C = "8084000004";
  static final String UNKNOWN_APDU_C = "8084000008";
  static final String APDU_R = "001122339000";
  static final String POWER_ON_DATA = "3B8880010000000000718100F9";
  private CardResource cardResource;
  private CardResourceReaderAdapter readerAdapter;
  private SmartCard smartCard;
  private CardReader reader;
  private ReaderSpi readerExtension;

  @Before
  public void setUp() throws Exception {
    reader = mock(CardReader.class);
    when(reader.getName()).thenReturn(CONTACT_READER_NAME);
    when(reader.isContactless()).thenReturn(false);

    smartCard = mock(SmartCard.class);
    when(smartCard.getPowerOnData()).thenReturn(POWER_ON_DATA);

    readerExtension = mock(CardResourcePluginAdapterTest.ReaderExtension.class);
    when(readerExtension.getName()).thenReturn(CONTACT_READER_NAME);
    when(readerExtension.isContactless()).thenReturn(false);
    when(readerExtension.transmitApdu(HexUtil.toByteArray(APDU_C)))
        .thenReturn(HexUtil.toByteArray(APDU_R));
    when(readerExtension.transmitApdu(
            argThat(
                new ArgumentMatcher<byte[]>() {
                  @Override
                  public boolean matches(byte[] arg) {
                    return !Arrays.equals(arg, HexUtil.toByteArray(APDU_C));
                  }
                })))
        .thenThrow(new CardIOException("Unknown APDU"));

    cardResource = mock(CardResource.class);
    when(cardResource.getReader()).thenReturn(reader);
    when(cardResource.getReaderExtension()).thenReturn((KeypleReaderExtension) readerExtension);
    when(cardResource.getSmartCard()).thenReturn(smartCard);

    readerAdapter = new CardResourceReaderAdapter(cardResource);
  }

  @Test
  public void getName_shouldReturnFormattedName() {
    String expectedName = cardResource.getReader().getName() + " (CardResource)";
    assertThat(readerAdapter.getName()).isEqualTo(expectedName);
  }

  @Test
  public void getCardResource_shouldReturnCardResource() {
    assertThat(readerAdapter.getCardResource()).isEqualTo(cardResource);
  }

  @Test
  public void openPhysicalChannel_hasNoInteraction() {
    readerAdapter.openPhysicalChannel();
    verifyZeroInteractions(reader);
    verify(readerExtension).getName();
    verifyNoMoreInteractions(readerExtension);
    verifyZeroInteractions(smartCard);
  }

  @Test
  public void closePhysicalChannel_hasNoInteraction() {
    readerAdapter.closePhysicalChannel();
    verifyZeroInteractions(reader);
    verify(readerExtension).getName();
    verifyNoMoreInteractions(readerExtension);
    verifyZeroInteractions(smartCard);
  }

  @Test
  public void isPhysicalChannelOpen_shouldReturnTrue() {
    assertThat(readerAdapter.isPhysicalChannelOpen()).isTrue();
  }

  @Test
  public void checkCardPresence_shouldReturnTrue() {
    assertThat(readerAdapter.checkCardPresence()).isTrue();
  }

  @Test
  public void getPowerOnData_shouldReturnCardPowerOnData() {
    String expectedPowerOnData = cardResource.getSmartCard().getPowerOnData();
    assertThat(readerAdapter.getPowerOnData()).isEqualTo(expectedPowerOnData);
    assertThat(readerAdapter.getPowerOnData()).isEqualTo(POWER_ON_DATA);
  }

  @Test
  public void isContactless_whenReaderIsContactless_shouldReturnTrue() {
    CardReader reader = mock(CardReader.class);
    when(reader.getName()).thenReturn(CONTACT_READER_NAME);
    when(reader.isContactless()).thenReturn(true);

    SmartCard smartCard = mock(SmartCard.class);

    CardResourcePluginAdapterTest.ReaderExtension readerExtension =
        mock(CardResourcePluginAdapterTest.ReaderExtension.class);
    when(readerExtension.getName()).thenReturn(CONTACTLESS_READER_NAME);
    when(readerExtension.isContactless()).thenReturn(true);

    CardResource contactCardResource = mock(CardResource.class);
    when(contactCardResource.getReader()).thenReturn(reader);
    when(contactCardResource.getReaderExtension()).thenReturn(readerExtension);
    when(contactCardResource.getSmartCard()).thenReturn(smartCard);

    CardResourceReaderAdapter contactAdapter = new CardResourceReaderAdapter(contactCardResource);
    assertThat(contactAdapter.isContactless())
        .isEqualTo(contactCardResource.getReader().isContactless());
    assertThat(contactAdapter.isContactless()).isTrue();
  }

  @Test
  public void isContactless_whenReaderIsContact_shouldReturnFalse() {
    // default cardResource contains a contact reader type
    assertThat(readerAdapter.isContactless()).isEqualTo(cardResource.getReader().isContactless());
    assertThat(readerAdapter.isContactless()).isFalse();
  }

  @Test
  public void transmitApdu_whenCardIsResponding_shouldReturnApduR() throws Exception {
    assertThat(readerAdapter.transmitApdu(HexUtil.toByteArray(APDU_C)))
        .isEqualTo(HexUtil.toByteArray(APDU_R));
  }

  @Test(expected = CardIOException.class)
  public void transmitApdu_whenCardIsNotResponding_shouldThrowCardIOException() throws Exception {
    readerAdapter.transmitApdu(HexUtil.toByteArray(UNKNOWN_APDU_C));
  }

  @Test
  public void getSelectedSmartCard_shouldReturnSmartcard() {
    assertThat(readerAdapter.getSelectedSmartCard()).isEqualTo(smartCard);
  }
}
