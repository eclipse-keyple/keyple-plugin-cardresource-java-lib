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

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.keyple.core.plugin.PluginIOException;
import org.eclipse.keyple.core.plugin.spi.PoolPluginSpi;
import org.eclipse.keyple.core.plugin.spi.reader.ReaderSpi;
import org.eclipse.keyple.core.service.resource.CardResource;
import org.eclipse.keyple.core.service.resource.CardResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter of {@link CardResourcePlugin} and {@link PoolPluginSpi}.
 *
 * @since 1.0.0
 */
final class CardResourcePluginAdapter implements CardResourcePlugin, PoolPluginSpi {

  private static final Logger logger = LoggerFactory.getLogger(CardResourcePluginAdapter.class);

  private final String pluginName;
  private final Collection<String> cardResourceProfileNames;
  private final CardResourceService cardResourceService;

  /**
   * Constructor.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service.
   * @param cardResourceProfileNames A collection of card resource profile names that represent the
   *     resources managed by the plugin adapter.
   * @since 1.0.0
   */
  CardResourcePluginAdapter(
      String pluginName,
      CardResourceService cardResourceService,
      Collection<String> cardResourceProfileNames) {
    this.pluginName = pluginName;
    this.cardResourceService = cardResourceService;
    this.cardResourceProfileNames = new ArrayList<String>(cardResourceProfileNames);
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getName() {
    return pluginName;
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
    CardResource cardResource;
    try {
      cardResource = cardResourceService.getCardResource(cardResourceProfileName);
    } catch (IllegalArgumentException e) {
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
