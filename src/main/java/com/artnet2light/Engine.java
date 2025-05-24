package com.artnet2light;


public class Engine {
    ArtNetSender ArtnetHandler; // Use broadcast or specific IP
    AudioCapture AudioEngine;

    Gain myGain = new Gain(0.00001);
    Smoother mySmoother = new Smoother(0.1,0.1);
    Gate myGate = new Gate(0.1);

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
            int output = this.processSound(amplitude);
            this.sendLight(output);
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
            ArtnetHandler.sendValue(1,8,267,value);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int processSound(double amplitude)
    {   
        double value = myGain.apply(amplitude);
        value = mySmoother.apply(value);
        value = myGate.tryGate(value);

        int output = (int) (value * 255);
        System.out.println(output);
        return output;
    }
}
