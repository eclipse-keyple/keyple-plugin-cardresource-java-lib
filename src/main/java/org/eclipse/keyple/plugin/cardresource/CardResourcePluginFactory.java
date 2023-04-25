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

import org.eclipse.keyple.core.common.KeyplePluginExtensionFactory;

/**
 * Card Resource Service specific {@link KeyplePluginExtensionFactory} to be provided to the Keyple
 * SmartCard service to register the Card Resource plugin, built by {@link
 * CardResourcePluginFactoryBuilder}.
 *
 * @since 1.0.0
 */
public interface CardResourcePluginFactory extends KeyplePluginExtensionFactory {}
