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

import org.calypsonet.terminal.reader.CardReader;
import org.calypsonet.terminal.reader.ConfigurableCardReader;
import org.calypsonet.terminal.reader.spi.CardReaderObservationExceptionHandlerSpi;
import org.eclipse.keyple.core.common.KeypleReaderExtension;
import org.eclipse.keyple.core.service.ObservablePlugin;
import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.PluginEvent;
import org.eclipse.keyple.core.service.SmartCardServiceProvider;
import org.eclipse.keyple.core.service.resource.spi.ReaderConfiguratorSpi;
import org.eclipse.keyple.core.service.spi.PluginObservationExceptionHandlerSpi;
import org.eclipse.keyple.core.service.spi.PluginObserverSpi;
import org.eclipse.keyple.core.util.HexUtil;
import org.eclipse.keyple.plugin.stub.StubPlugin;
import org.eclipse.keyple.plugin.stub.StubPluginFactoryBuilder;
import org.eclipse.keyple.plugin.stub.StubReader;
import org.eclipse.keyple.plugin.stub.StubSmartCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class to manage Stub plugin, readers and virtual smartcards. */
public class StubUtil {

  static final String ISO_CARD_PROTOCOL = "ISO_7816_CARD";
  static final String AID = "315449432E49434131";
  static final String CONTACTLESS_READER_NAME = "contactLessReader";
  static final String CONTACT_READER_NAME = "contactReader";
  static final String APDU_C = "8084000004";
  static final String UNKNOWN_APDU_C = "8084000008";
  static final String APDU_R = "001122339000";
  static final String POWER_ON_DATA = "3B8880010000000000718100F9";
  private Plugin plugin;
  private CardReader cardReader;

  StubUtil() {}

  StubSmartCard getStubCard() {
    return StubSmartCard.builder()
        .withPowerOnData(HexUtil.toByteArray(POWER_ON_DATA))
        .withProtocol(ISO_CARD_PROTOCOL)
        .withSimulatedCommand("00A4040005AABBCCDDEE00", "6A82")
        .withSimulatedCommand(
            "00A4040009" + AID + "00",
            "6F238409" + AID + "A516BF0C13C708000000001122334453070A3C23121410019000")
        // get challenge
        .withSimulatedCommand(APDU_C, APDU_R)
        .build();
  }

  void initPluginAndReader() throws InterruptedException {
    plugin =
        SmartCardServiceProvider.getService()
            .registerPlugin(StubPluginFactoryBuilder.builder().build());

    ObservablePlugin observablePlugin = (ObservablePlugin) plugin;

    observablePlugin.setPluginObservationExceptionHandler(
        new PluginObservationExceptionHandlerSpi() {
          @Override
          public void onPluginObservationError(String pluginName, Throwable e) {
            // NOP
          }
        });

    observablePlugin.addObserver(
        new PluginObserverSpi() {
          @Override
          public void onPluginEvent(PluginEvent pluginEvent) {
            // NOP
          }
        });

    plugin.getExtension(StubPlugin.class).plugReader(CONTACTLESS_READER_NAME, true, null);
    plugin.getExtension(StubPlugin.class).plugReader(CONTACT_READER_NAME, false, null);

    waitForPluginToBeReady();

    cardReader = plugin.getReader(CONTACT_READER_NAME);
    ConfigurableCardReader configurableCardReader = (ConfigurableCardReader) cardReader;
    configurableCardReader.activateProtocol(ISO_CARD_PROTOCOL, ISO_CARD_PROTOCOL);

    plugin.getReaderExtension(StubReader.class, cardReader.getName()).insertCard(getStubCard());
  }

  private void waitForPluginToBeReady() throws InterruptedException {
    long startTime = System.currentTimeMillis();
    while (plugin.getReaders().size() != 2) {
      long elapsedTime = System.currentTimeMillis() - startTime;
      if (elapsedTime >= 2000) {
        throw new IllegalStateException("Timeout for the Stub plugin to be ready");
      }
      Thread.sleep(1); // Sleep for a short period to avoid busy waiting
    }
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public CardReader getCardReader() {
    return cardReader;
  }

  static class ReaderConfigurator implements ReaderConfiguratorSpi {
    private static final Logger logger = LoggerFactory.getLogger(ReaderConfigurator.class);

    /**
     * (private)<br>
     * Constructor.
     */
    ReaderConfigurator() {}

    /** {@inheritDoc} */
    @Override
    public void setupReader(CardReader reader) {
      // Configure the reader with parameters suitable for contact operations.
      try {
        KeypleReaderExtension readerExtension =
            SmartCardServiceProvider.getService()
                .getPlugin(reader)
                .getReaderExtension(KeypleReaderExtension.class, reader.getName());
      } catch (Exception e) {
        logger.error("Exception raised while setting up the reader {}", reader.getName(), e);
      }
    }
  }

  /** Class implementing the exception handler SPIs for plugin and reader monitoring. */
  static class PluginAndReaderExceptionHandler
      implements PluginObservationExceptionHandlerSpi, CardReaderObservationExceptionHandlerSpi {

    @Override
    public void onPluginObservationError(String pluginName, Throwable e) {
      // NOP
    }

    @Override
    public void onReaderObservationError(String pluginName, String readerName, Throwable e) {
      // NOP
    }
  }
}
