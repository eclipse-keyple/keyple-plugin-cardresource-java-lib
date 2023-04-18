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
import org.eclipse.keyple.core.util.Assert;

/**
 * This class is a builder for {@link CardResourcePluginFactory} instances.
 *
 * <p>It allows to add reader card resource profile names to the factory, which can then create a
 * {@link CardResourcePlugin} to allocate readers based on the specified card resource profiles.
 *
 * @since 1.0.0
 */
public final class CardResourcePluginFactoryBuilder {

  /** Private constructor to force the use of the static factory method {@link #builder()}. */
  private CardResourcePluginFactoryBuilder() {}

  /**
   * Creates builder to build a {@link CardResourcePluginFactory}.
   *
   * @return created builder
   * @since 1.0.0
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build a {@link CardResourcePluginFactory}.
   *
   * @since 1.0.0
   */
  public static class Builder {

    private final Set<String> cardResourceProfileNames = new TreeSet<String>();

    /** Constructs an empty Builder. */
    private Builder() {}

    /**
     * Adds one or more reader card resource profile names to the builder.
     *
     * @param cardResourceProfileNames The card resource profile names to add.
     * @return This builder instance, to allow method chaining.
     * @throws IllegalArgumentException If the given references array is null, empty, or contains
     *     null or empty elements.
     * @since 1.0.0
     */
    public Builder addReferences(String... cardResourceProfileNames) {
      Assert.getInstance()
          .notNull(cardResourceProfileNames, "cardResourceProfileNames")
          .notEmpty(Arrays.asList(cardResourceProfileNames), "cardResourceProfileNames");
      for (String cardResourceProfileName : cardResourceProfileNames) {
        Assert.getInstance()
            .notNull(cardResourceProfileName, "cardResourceProfileName")
            .notEmpty(cardResourceProfileName, "cardResourceProfileName");
        this.cardResourceProfileNames.add(cardResourceProfileName);
      }
      return this;
    }

    /**
     * Returns an instance of {@link CardResourcePluginFactory} created from the fields set on this
     * builder.
     *
     * @return A {@link CardResourcePluginFactory}
     * @since 1.0.0
     */
    public CardResourcePluginFactory build() {
      return new CardResourcePluginFactoryAdapter(cardResourceProfileNames);
    }
  }
}
