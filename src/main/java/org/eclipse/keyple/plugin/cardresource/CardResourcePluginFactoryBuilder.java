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
 * Builds instances of {@link CardResourcePluginFactory}.
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
   * Creates a builder to build a {@link CardResourcePluginFactory}.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service to use. It must be configured and started.
   * @param cardResourceProfileNames The card resource profile names to use.
   * @return A new builder instance.
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
   * Creates a builder to build a {@link CardResourcePluginFactory}.
   *
   * @param pluginName The name of the plugin.
   * @param cardResourceService The card resource service to use. It must be configured and started.
   * @param cardResourceProfileName The card resource profile name to use.
   * @return A new builder instance.
   * @throws IllegalArgumentException If the one of the given references is null or empty.
   * @since 1.0.0
   */
  public static Builder builder(
      String pluginName, CardResourceService cardResourceService, String cardResourceProfileName) {
    return builder(pluginName, cardResourceService, Collections.singleton(cardResourceProfileName));
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
