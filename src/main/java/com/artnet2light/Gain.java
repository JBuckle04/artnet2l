package com.artnet2light;

public class Gain {
    private volatile double gainFactor;

    public Gain(double gainFactor) {
        this.gainFactor = gainFactor;
    }

    public double apply(double input) {
        double output = input * gainFactor;
        // Clamp to 0.0 - 1.0 if needed
        if (output > 1.0) 
        {
            output = 1.0;
        }
        if (output < 0.0){
            output = 0.0;
            }
        return output;
    }

    public void setGain(double gainFactor) {
        this.gainFactor = gainFactor;
    }
}
