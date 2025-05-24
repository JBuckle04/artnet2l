package com.artnet2light;

import java.net.InetAddress;
import java.net.NetworkInterface;

import ch.bildspur.artnet.*;
public class ArtNetSender {
    private ArtNetClient artnet;

    public ArtNetSender() throws Exception {

        NetworkInterface ni = NetworkInterface.getByName("en0");
        InetAddress address = ni.getInetAddresses().nextElement();

        
        artnet = new ArtNetClient();
        artnet.start(address);
        artnet.getArtNetServer().setBroadcastAddress("127.0.0.1");
        System.out.println(artnet.getArtNetServer().getIPAddress());
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

        artnet.broadcastDmx( subnet, universe, dmxData);
        //artnet.broadcastDmx(subnet, universe, dmxData);
    }

    public void close() {
        artnet.stop();
    }
}
