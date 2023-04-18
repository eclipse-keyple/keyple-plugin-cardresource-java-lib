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

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.keyple.core.service.PoolPlugin;
import org.eclipse.keyple.core.service.SmartCardServiceProvider;
import org.junit.Test;

public class CardResourcePluginFactoryBuilderTest {

  @Test
  public void build_whenAddsReferences_thenPluginContainsReferences() {
    CardResourcePluginFactoryBuilder.Builder builder = CardResourcePluginFactoryBuilder.builder();
    Set<String> expectedCardResourceProfileNames =
        new HashSet<String>(Arrays.asList("profile1", "profile2"));
    builder.addReferences("profile1", "profile2");
    PoolPlugin poolPlugin =
        (PoolPlugin) SmartCardServiceProvider.getService().registerPlugin(builder.build());
    assertThat(poolPlugin.getReaderGroupReferences()).isEqualTo(expectedCardResourceProfileNames);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAddsEmptyReferences_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.Builder builder = CardResourcePluginFactoryBuilder.builder();
    builder.addReferences();
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAddsNullReferences_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.Builder builder = CardResourcePluginFactoryBuilder.builder();
    String[] references = null;
    builder.addReferences(references);
  }

  @Test(expected = IllegalArgumentException.class)
  public void builder_whenAddsNullReference_thenThrowIAE() {
    CardResourcePluginFactoryBuilder.Builder builder = CardResourcePluginFactoryBuilder.builder();
    String[] references = {"profile1", null, "profile2"};
    builder.addReferences(references);
  }
}
