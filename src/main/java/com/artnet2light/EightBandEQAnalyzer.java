package com.artnet2light;
import org.jtransforms.fft.DoubleFFT_1D;

public class EightBandEQAnalyzer {

    private final int sampleRate;
    private final int frameSize;
    private final DoubleFFT_1D fft;

    // Frequency bands in Hz (approximate, adjust as needed)
    private final int[] bandEdges = {60, 250, 500, 1000, 2000, 4000, 8000, 16000};

    public EightBandEQAnalyzer(int sampleRate, int frameSize) {
        this.sampleRate = sampleRate;
        this.frameSize = frameSize;
        this.fft = new DoubleFFT_1D(frameSize);
    }

    public double[][] analyze(float[] audioSamples) {
        int numFrames = audioSamples.length / frameSize;
        double[][] bandLevels = new double[numFrames][8];

        for (int i = 0; i < numFrames; i++) {
            float[] frame = new float[frameSize];
            System.arraycopy(audioSamples, i * frameSize, frame, 0, frameSize);

            // Convert to double for FFT
            double[] fftInput = new double[frameSize * 2]; // Real + Imag
            for (int j = 0; j < frameSize; j++) {
                fftInput[2 * j] = frame[j];     // Real part
                fftInput[2 * j + 1] = 0.0;       // Imaginary part
            }

            fft.complexForward(fftInput);

            // Calculate magnitudes and group into bands
            double[] bandMagnitudes = new double[8];
            int[] bandCounts = new int[8];

            for (int bin = 0; bin < frameSize / 2; bin++) {
                double re = fftInput[2 * bin];
                double im = fftInput[2 * bin + 1];
                double magnitude = Math.sqrt(re * re + im * im);

                double freq = bin * sampleRate / (double) frameSize;
                int band = getBandIndex(freq);
                if (band >= 0) {
                    bandMagnitudes[band] += magnitude;
                    bandCounts[band]++;
                }
            }

            // Average the magnitudes per band
            for (int b = 0; b < 8; b++) {
                bandLevels[i][b] = bandCounts[b] > 0 ? bandMagnitudes[b] / bandCounts[b] : 0;
            }
        }

        return bandLevels;
    }

    private int getBandIndex(double freq) {
        for (int i = 0; i < bandEdges.length; i++) {
            if (freq < bandEdges[i]) {
                return i;
            }
        }
        return 7; // Last band (16kHz+)
    }
}
