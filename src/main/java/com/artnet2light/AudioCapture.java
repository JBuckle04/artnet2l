package com.artnet2light;
import javax.sound.sampled.*;

public class AudioCapture {
    private TargetDataLine line;
    private volatile double amplitude;

    public void start(){
        AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
        try {
            line = AudioSystem.getTargetDataLine(format);
            line.open(format);
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        line.start();

        byte[] buffer = new byte[1024];

        while (true) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            amplitude = calculateAmplitude(buffer, bytesRead);
            //System.out.println("Amplitude: " + amplitude);

            
            try {
                Thread.sleep(50); //TODO change this for real time in deployment
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // You would map this amplitude to DMX here
        }
    }

    public double getAmp()
    {
        return amplitude;
    }

    private double calculateAmplitude(byte[] buffer, int bytesRead) {
        double sum = 0;
        for (int i = 0; i < bytesRead - 1; i += 2) {
            int value = (buffer[i + 1] << 8) | (buffer[i] & 0xff);
            sum += Math.abs(value);
        }
        return sum / (bytesRead / 2);
    }
}
