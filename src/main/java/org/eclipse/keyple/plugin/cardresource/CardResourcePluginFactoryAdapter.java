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

import java.util.Collection;
import org.eclipse.keyple.core.common.CommonApiProperties;
import org.eclipse.keyple.core.plugin.PluginApiProperties;
import org.eclipse.keyple.core.plugin.spi.PoolPluginFactorySpi;
import org.eclipse.keyple.core.plugin.spi.PoolPluginSpi;
import org.eclipse.keyple.core.service.resource.CardResourceService;

/**
 * Adapter of {@link CardResourcePluginFactory} and {@link PoolPluginFactorySpi}.
 *
 * @since 1.0.0
 */
final class CardResourcePluginFactoryAdapter
    implements CardResourcePluginFactory, PoolPluginFactorySpi {

  private final String pluginName;
  private final CardResourceService cardResourceService;
  private final Collection<String> cardResourceProfileNames;

  /**
   * Constructor.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service.
   * @param cardResourceProfileNames A collection of card resource profile names to be used by the
   *     plugin.
   * @since 1.0.0
   */
  CardResourcePluginFactoryAdapter(
      String pluginName,
      CardResourceService cardResourceService,
      Collection<String> cardResourceProfileNames) {
    this.pluginName = pluginName;
    this.cardResourceService = cardResourceService;
    this.cardResourceProfileNames = cardResourceProfileNames;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getPluginApiVersion() {
    return PluginApiProperties.VERSION;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getCommonApiVersion() {
    return CommonApiProperties.VERSION;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public String getPoolPluginName() {
    return pluginName;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public PoolPluginSpi getPoolPlugin() {
    return new CardResourcePluginAdapter(pluginName, cardResourceService, cardResourceProfileNames);
  }
}
