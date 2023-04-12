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
import java.util.SortedSet;
import java.util.TreeSet;
import org.eclipse.keyple.core.util.Assert;

/**
 * This class is a builder for {@link CardResourcePluginFactory} instances.
 *
 * <p>It allows to add reader group references to the factory, which can then allocate readers based
 * on the specified groups.
 *
 * @since 1.0.0
 */
public final class CardResourcePluginFactoryBuilder {

  private final SortedSet<String> readerGroupReferences = new TreeSet<String>();

  /** Private constructor to force the use of the static factory method {@link #getInstance()}. */
  private CardResourcePluginFactoryBuilder() {}

  /**
   * Creates a new instance of the builder.
   *
   * @return a new instance of the builder.
   * @since 1.0.0
   */
  public static CardResourcePluginFactoryBuilder getInstance() {
    return new CardResourcePluginFactoryBuilder();
  }

  /**
   * Adds a reader group reference to the builder.
   *
   * @param reference the reference of the reader group to add.
   * @return this builder instance, to allow method chaining.
   * @throws IllegalArgumentException if the given reference is null.
   * @since 1.0.0
   */
  public CardResourcePluginFactoryBuilder addReaderGroupReference(String reference) {
    Assert.getInstance().notNull(reference, "reference");
    readerGroupReferences.add(reference);
    return this;
  }

  /**
   * Adds a collection of reader group references to the builder.
   *
   * @param references the collection of references to add to the builder.
   * @return this builder instance, to allow method chaining.
   * @throws IllegalArgumentException if the given collection is null or contains null elements.
   * @since 1.0.0
   */
  public CardResourcePluginFactoryBuilder addReaderGroupReferences(Collection<String> references) {
    Assert.getInstance().notEmpty(references, "references");
    readerGroupReferences.addAll(references);
    return this;
  }

  /**
   * Builds a new instance of the {@link CardResourcePluginFactory}.
   *
   * @return a new instance of the {@link CardResourcePluginFactory} configured with the reader
   *     group references added to this builder instance.
   * @since 1.0.0
   */
  public CardResourcePluginFactory build() {
    return new CardResourcePluginFactoryAdapter(readerGroupReferences);
  }
}
