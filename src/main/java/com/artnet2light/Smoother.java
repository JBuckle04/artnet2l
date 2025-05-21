package com.artnet2light;

public class Smoother {
    private double currentValue = 0;
    private final double attackRate;
    private final double decayRate;

    public Smoother(double attackRate, double decayRate) {
        this.attackRate = attackRate;
        this.decayRate = decayRate;
    }

    public double apply(double target) {
        
        if (target > currentValue) {
            currentValue += (target - currentValue) * attackRate;
        } else {
            currentValue += (target - currentValue) * decayRate;
        }

        // Clamp currentValue between 0 and 1 to avoid drift
        currentValue = Math.min(1.0, Math.max(0.0, currentValue));

        return currentValue;
    }
}
