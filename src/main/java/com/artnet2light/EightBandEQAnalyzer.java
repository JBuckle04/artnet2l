package com.artnet2light;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jtransforms.fft.DoubleFFT_1D;

public class EightBandEQAnalyzer {

    private final int sampleRate;
    private final int fftSize;
    private final DoubleFFT_1D fft;

    // Frequency bands in Hz (approximate, adjust as needed)
    private final int[] bandEdges = {60, 250, 500, 1000, 2000, 4000, 8000, 16000};

       public EightBandEQAnalyzer(int sampleRate, int fftSize) {
        this.sampleRate = sampleRate;
        this.fftSize = fftSize;
        this.fft = new DoubleFFT_1D(fftSize);
    }

    public double[] analyze(byte[] audioBuffer) {
        double[] samples = new double[fftSize];
        int sampleCount = Math.min(audioBuffer.length / 2, fftSize);

        for (int i = 0; i < sampleCount; i++) {
            int low = audioBuffer[2 * i] & 0xFF;
            int high = audioBuffer[2 * i + 1];
            int sample = (high << 8) | low;
            samples[i] = sample / 32768.0; // Normalize to [-1, 1]
        }

        double[] fftData = new double[fftSize * 2];
        System.arraycopy(samples, 0, fftData, 0, samples.length);

        fft.realForwardFull(fftData);

        double[] magnitude = new double[fftSize / 2];
        for (int i = 0; i < magnitude.length; i++) {
            double re = fftData[2 * i];
            double im = fftData[2 * i + 1];
            magnitude[i] = Math.sqrt(re * re + im * im);
        }

        return computeEQBands(magnitude);
    }

    public Map<String, Double> getNamedBands(byte[] audioBuffer) {
        double[] levels = analyze(audioBuffer);

        String[] bandLabels = {
            "Sub Bass (20–60Hz)",
            "Bass (60–250Hz)",
            "Low Mid (250–500Hz)",
            "Mid (500–2kHz)",
            "Upper Mid (2–4kHz)",
            "Presence (4–6kHz)",
            "Brilliance (6–16kHz)",
            "Air (16–20kHz)"
        };

        Map<String, Double> bandMap = new LinkedHashMap<>();
        for (int i = 0; i < bandLabels.length; i++) {
            bandMap.put(bandLabels[i], levels[i]);
        }

        return bandMap;
    }

    public void printNamedBands(byte[] audioBuffer) {
        Map<String, Double> bands = getNamedBands(audioBuffer);
        System.out.println("8-Band EQ Analysis:");
        bands.forEach((band, level) ->
            System.out.printf("  %-22s : %.3f%n", band, level)
        );
    }

    private double[] computeEQBands(double[] magnitude) {
        int nyquist = sampleRate / 2;
        double[] eqBands = new double[8];

        // Define the 8 bands (in Hz)
        int[] bandLimits = {20, 60, 250, 500, 2000, 4000, 6000, 16000, 20000};

        for (int b = 0; b < 8; b++) {
            double sum = 0;
            int count = 0;
            int startFreq = bandLimits[b];
            int endFreq = bandLimits[b + 1];

            int startBin = (int) Math.floor(startFreq * magnitude.length / nyquist);
            int endBin = (int) Math.ceil(endFreq * magnitude.length / nyquist);

            for (int i = startBin; i < endBin && i < magnitude.length; i++) {
                sum += magnitude[i];
                count++;
            }

            eqBands[b] = count > 0 ? sum / count : 0.0;
        }

        return eqBands;
    }
}