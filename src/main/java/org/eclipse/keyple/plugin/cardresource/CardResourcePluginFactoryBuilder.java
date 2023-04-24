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

import java.util.*;
import org.eclipse.keyple.core.service.resource.CardResourceService;
import org.eclipse.keyple.core.util.Assert;

/**
 * This class is a builder for {@link CardResourcePluginFactory} instances.
 *
 * <p>It allows to set the plugin name and one or more reader card resource profile names to the
 * factory, which can then create a {@link CardResourcePlugin} to allocate readers based on the
 * specified card resource profiles.
 *
 * @since 1.0.0
 */
public final class CardResourcePluginFactoryBuilder {

  /**
   * Private constructor to force the use of the static factory method {@link #builder(String,
   * CardResourceService, String)}.
   */
  private CardResourcePluginFactoryBuilder() {}

  /**
   * Creates a builder to build a {@link CardResourcePluginFactory}. Sets the plugin name and one or
   * more reader card resource profile names.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service.
   * @param cardResourceProfileNames The card resource profile names to set.
   * @return created builder
   * @throws IllegalArgumentException If the one of the given references is null, empty, or contains
   *     null or empty elements.
   * @since 1.0.0
   */
  public static Builder builder(
      String pluginName,
      CardResourceService cardResourceService,
      Collection<String> cardResourceProfileNames) {
    Assert.getInstance()
        .notEmpty(pluginName, "pluginName")
        .notNull(cardResourceService, "cardResourceService")
        .notEmpty(cardResourceProfileNames, "cardResourceProfileNames");
    for (String cardResourceProfileName : cardResourceProfileNames) {
      Assert.getInstance().notEmpty(cardResourceProfileName, "cardResourceProfileName");
    }
    return new Builder(pluginName, cardResourceService, cardResourceProfileNames);
  }

  /**
   * Creates a builder to build a {@link CardResourcePluginFactory}. Sets the plugin name and a
   * single reader card resource profile names.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service.
   * @param cardResourceProfileName The card resource profile name to set.
   * @return created builder
   * @throws IllegalArgumentException If the one of the given references is null, empty, or contains
   *     null or empty elements.
   * @since 1.0.0
   */
  public static Builder builder(
      String pluginName, CardResourceService cardResourceService, String cardResourceProfileName) {
    Assert.getInstance()
        .notEmpty(pluginName, "pluginName")
        .notEmpty(cardResourceProfileName, "cardResourceProfileName");
    return new Builder(
        pluginName, cardResourceService, Collections.singleton(cardResourceProfileName));
  }

  /**
   * Builder to build a {@link CardResourcePluginFactory}.
   *
   * @since 1.0.0
   */
  public static class Builder {

    private final String pluginName;
    private final CardResourceService cardResourceService;
    private final Collection<String> cardResourceProfileNames;

    /** Constructs a Builder with the factory parameters. */
    private Builder(
        String pluginName,
        CardResourceService cardResourceService,
        Collection<String> cardResourceProfileNames) {
      this.pluginName = pluginName;
      this.cardResourceService = cardResourceService;
      this.cardResourceProfileNames = cardResourceProfileNames;
    }

    /**
     * Returns an instance of {@link CardResourcePluginFactory} created from the fields set on this
     * builder.
     *
     * @return A {@link CardResourcePluginFactory}
     * @since 1.0.0
     */
    public CardResourcePluginFactory build() {
      return new CardResourcePluginFactoryAdapter(
          pluginName, cardResourceService, cardResourceProfileNames);
    }
  }
}
