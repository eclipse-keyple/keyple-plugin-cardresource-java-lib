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

import org.eclipse.keyple.core.common.KeypleReaderExtension;

/**
 * API of the <b>ard Resource reader</b> provided by the <b>Card Resource Plugin</b>.
 *
 * <p>It is a {@link KeypleReaderExtension} of a Keyple <b>Reader</b> (not observable).
 *
 * @since 1.0.0
 */
public interface CardResourceReader extends KeypleReaderExtension {}
