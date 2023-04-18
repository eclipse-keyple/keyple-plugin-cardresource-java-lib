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

import java.util.Set;
import org.eclipse.keyple.core.common.CommonApiProperties;
import org.eclipse.keyple.core.plugin.PluginApiProperties;
import org.eclipse.keyple.core.plugin.spi.PoolPluginFactorySpi;
import org.eclipse.keyple.core.plugin.spi.PoolPluginSpi;

/**
 * The Card Resource plugin factory extension adapter provides an implementation of the {@link
 * CardResourcePluginFactory} and {@link PoolPluginFactorySpi} interfaces.
 *
 * <p>It is used to create an instance of the {@link CardResourcePluginAdapter} class, which
 * provides allocation mechanisms for readers based on card resource profile names via a Pool Plugin
 * interface.
 *
 * @since 1.0.0
 */
final class CardResourcePluginFactoryAdapter
    implements CardResourcePluginFactory, PoolPluginFactorySpi {

  /**
   * The unique name of the plugin.
   *
   * @since 1.0.0
   */
  static final String PLUGIN_NAME = "CardResourcePlugin";

  private final Set<String> cardResourceProfileNames;

  /**
   * Constructs a new CardResourcePluginFactoryAdapter with the specified set of card resource
   * profile names.
   *
   * @param cardResourceProfileNames A set of card resource profile names to be used by the plugin.
   * @since 1.0.0
   */
  CardResourcePluginFactoryAdapter(Set<String> cardResourceProfileNames) {
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
    return PLUGIN_NAME;
  }

  /**
   * {@inheritDoc}
   *
   * @since 1.0.0
   */
  @Override
  public PoolPluginSpi getPoolPlugin() {
    return new CardResourcePluginAdapter(cardResourceProfileNames);
  }
}
