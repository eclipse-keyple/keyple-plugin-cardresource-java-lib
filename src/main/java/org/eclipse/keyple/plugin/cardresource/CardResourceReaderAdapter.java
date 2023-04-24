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

import org.calypsonet.terminal.reader.selection.spi.SmartCard;
import org.eclipse.keyple.core.plugin.CardIOException;
import org.eclipse.keyple.core.plugin.ReaderIOException;
import org.eclipse.keyple.core.plugin.spi.reader.PoolReaderSpi;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.resource.CardResource;
import org.eclipse.keyple.core.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link CardResourceReader}.
 *
 * @since 1.0.0
 */
final class CardResourceReaderAdapter implements CardResourceReader, PoolReaderSpi {

  private static final Logger logger = LoggerFactory.getLogger(CardResourceReaderAdapter.class);

  private CardResource cardResource;
  private ReaderSpi readerSpi;
  private SmartCard smartcard;
  private final String name;

  /**
   * This constructor should only be called by allocateReader from {@link CardResourcePluginAdapter}
   *
   * @since 1.0.0
   */
  CardResourceReaderAdapter(CardResource cardResource) {
    this.cardResource = cardResource;
    readerSpi = (ReaderSpi) cardResource.getReaderExtension();
    smartcard = cardResource.getSmartCard();
    name = readerSpi.getName() + " (CardResource)";
  }

  /**
   * @return The {@link CardResource} associated with this reader.
   * @since 1.0.0
   */
  CardResource getCardResource() {
    return cardResource;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Do not nothing since the physical channel opening is implicit through the Card Resource
   * Service allocation process.
   *
   * @since 1.0.0
   */
  @Override
  public void openPhysicalChannel() {
    if (logger.isTraceEnabled()) {
      logger.trace("Open physical channel requested.");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Do not nothing since the physical channel closing is implicit through the Card Resource
   * Service de-allocation process.
   *
   * @since 1.0.0
   */
  @Override
  public void closePhysicalChannel() {
    if (logger.isTraceEnabled()) {
      logger.trace("Close physical channel requested.");
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public boolean isPhysicalChannelOpen() {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public boolean checkCardPresence() {
    if (logger.isTraceEnabled()) {
      logger.trace("Check card presence requested.");
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getPowerOnData() {
    String powerOnData = smartcard.getPowerOnData();
    if (logger.isTraceEnabled()) {
      logger.trace("Get power on requested. ATR = {}", powerOnData);
    }
    return powerOnData;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public byte[] transmitApdu(byte[] apduIn) throws ReaderIOException, CardIOException {
    if (logger.isTraceEnabled()) {
      logger.trace("APDU_REQ = {}", HexUtil.toHex(apduIn));
    }
    return readerSpi.transmitApdu(apduIn);
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public boolean isContactless() {
    return readerSpi.isContactless();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Unlinks the card resource by setting the cardResource, readerSpi, and smartcard references
   * to null.
   *
   * @since 1.0.0
   */
  @Override
  public void onUnregister() {
    cardResource = null;
    readerSpi = null;
    smartcard = null;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public Object getSelectedSmartCard() {
    return smartcard;
  }
}
