/* **************************************************************************************
 * Copyright (c) 2020 Calypso Networks Association https://calypsonet.org/
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

import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.keyple.core.plugin.PluginIOException;
import org.eclipse.keyple.core.plugin.spi.PoolPluginSpi;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.resource.CardResource;
import org.eclipse.keyple.core.service.resource.CardResourceService;
import org.eclipse.keyple.core.service.resource.CardResourceServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
final class CardResourcePluginAdapter implements CardResourcePlugin, PoolPluginSpi {

  private static final Logger logger = LoggerFactory.getLogger(CardResourcePluginAdapter.class);

  private final SortedSet<String> cardResourceProfileNames;
  private final CardResourceService cardResourceService;

  CardResourcePluginAdapter(SortedSet<String> cardResourceProfileNames) {
    this.cardResourceProfileNames = cardResourceProfileNames;
    cardResourceService = CardResourceServiceProvider.getService();
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getName() {
    return CardResourcePluginFactoryAdapter.PLUGIN_NAME;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public SortedSet<String> getReaderGroupReferences() {
    return new TreeSet<String>(cardResourceProfileNames);
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public ReaderSpi allocateReader(String cardResourceProfileName) throws PluginIOException {

    if (logger.isTraceEnabled()) {
      logger.trace("Reader allocation requested. GROUP_REFERENCE = {}", cardResourceProfileName);
    }

    CardResource cardResource = cardResourceService.getCardResource(cardResourceProfileName);

    if (cardResource == null) {
      throw new PluginIOException(
          "No card resource available for profile name " + cardResourceProfileName);
    }

    return new CardResourceReaderAdapter(cardResource);
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public void releaseReader(ReaderSpi readerSpi) {
    if (logger.isTraceEnabled()) {
      logger.trace("Reader release request READER_NAME = {}.", readerSpi.getName());
    }
    CardResource cardResource = ((CardResourceReaderAdapter) readerSpi).getCardResource();
    if (cardResource != null) {
      cardResourceService.releaseCardResource(cardResource);
    } else {
      logger.warn("The provided reader was not found: {}", readerSpi.getName());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public void onUnregister() {
    // NOP
  }
}
