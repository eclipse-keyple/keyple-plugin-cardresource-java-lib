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

import org.eclipse.keyple.core.common.KeyplePluginExtension;

/**
 * The {@link CardResourcePlugin} interface provides an API for the <b>Card Resource Plugin</b>,
 * which is a {@link KeyplePluginExtension} of a Keyple <b>{@link
 * org.eclipse.keyple.core.service.PoolPlugin}</b>.
 *
 * <p>The Card Resource Plugin provides a way to allocate readers in which a card is inserted and
 * selected. It is based on the Keyple <b>Card Resource Service</b>.
 *
 * @since 1.0.0
 */
public interface CardResourcePlugin extends KeyplePluginExtension {}
