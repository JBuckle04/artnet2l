package com.artnet2light;

import ch.bildspur.artnet.*;
public class ArtNetSender {
    private ArtNetClient artnet;

    public ArtNetSender() throws Exception {

        artnet = new ArtNetClient();
        artnet.start();
    }

    /**
     * Sends a DMX value to a specific channel (1-based index, 1–512).
     */
    public void sendValue(int subnet, int universe, int channel, int value) {
        if (channel < 1 || channel > 512) {
            throw new IllegalArgumentException("DMX channel must be in the range 1–512");
        }
        byte[] dmxData = new byte[512];
        dmxData[channel - 1] = (byte) (value & 0xFF); // DMX channels are 1-based

        artnet.unicastDmx("localhost", subnet, universe, dmxData);
    }

    public void close() {
        artnet.stop();
    }
}
