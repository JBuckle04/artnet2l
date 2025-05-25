package com.artnet2light;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

public class AudioCapture {
    private TargetDataLine line;
    private volatile double amplitude;
    private EightBandEQAnalyzer analyzer;

    private final int sampleRate = 44100;
    private final int frameSize = 2048; // FFT frame
    private final int byteDepth = 2; // 16-bit
    private final int channelCount = 1;
    private double[][] eqOut;

    public AudioCapture() {
        analyzer = new EightBandEQAnalyzer(sampleRate, frameSize);
    }

    public void start() {
        AudioFormat format = new AudioFormat(sampleRate, 16, channelCount, true, true);

        try {
            line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }

        byte[] byteBuffer = new byte[1024];
        List<Float> sampleBuffer = new ArrayList<>();

        while (true) {
            int bytesRead = line.read(byteBuffer, 0, byteBuffer.length);

            for (int i = 0; i < bytesRead - 1; i += 2) {
                int sample = (byteBuffer[i] << 8) | (byteBuffer[i + 1] & 0xFF);
                sampleBuffer.add(sample / 32768f); // Normalize to [-1, 1]
            }

            // Process when we have a full frame
            if (sampleBuffer.size() >= frameSize) {
                float[] frame = new float[frameSize];
                for (int i = 0; i < frameSize; i++) {
                    frame[i] = sampleBuffer.get(i);
                }

                // Run EQ
                eqOut = analyzer.analyze(frame);

                // Example: print first band
                System.out.printf("EQ Band 0 Level: %.2f\n", eqOut[0][0]);

                // Remove used samples
                sampleBuffer.subList(0, frameSize).clear();
            }

            // Optional: track raw amplitude for other uses
            amplitude = calculateAmplitude(byteBuffer, bytesRead);

            try {
                Thread.sleep(50); // match polling interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double getAmp() {
        return this.amplitude;
    }

    public double[][] getEq() {
        return this.eqOut;
    }

    private double calculateAmplitude(byte[] buffer, int bytesRead) {
        double sum = 0;
        for (int i = 0; i < bytesRead - 1; i += 2) {
            int value = (buffer[i] << 8) | (buffer[i + 1] & 0xFF);
            sum += Math.abs(value);
        }
        return sum / (bytesRead / 2);
    }
}
