package com.artnet2light;

import javax.sound.midi.SysexMessage;

public class Engine {
    ArtNetSender ArtnetHandler; // Use broadcast or specific IP
    AudioCapture AudioEngine;
    public static void main(String[] args) throws Exception {

        new Engine().start();
    }
    
    public void start() throws Exception{
        ArtnetHandler = new ArtNetSender(); // Use broadcast or specific IP
        AudioEngine = new AudioCapture();

        new Thread(() -> AudioEngine.start()).start();
        new Thread(() -> coordinate()).start();        

    }

    public void coordinate()
    {

        while (true) {
            double amplitude = AudioEngine.getAmp();
            int value = this.processSound(amplitude);
            this.sendLight(value);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendLight(int value)
    {
        try {
            ArtnetHandler.sendValue(0,0,1,value);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int processSound(double amplitude)
    {   
        Smoother mySmoother = new Smoother(0.5,0.05,255);
        Gate myGate = new Gate(110);

        int value = mySmoother.updateAndScale(amplitude);
        value = myGate.tryGate(value);
        System.out.println(value);
        return value;
    }
}
