package com.artnet2light;

public class Smoother {
    private double currentValue = 0;
    private final double attackRate;
    private final double decayRate;
    private final int maxDMXValue;

    public Smoother(double attackRate, double decayRate, int maxDMXValue) {
        this.attackRate = attackRate;
        this.decayRate = decayRate;
        this.maxDMXValue = maxDMXValue;
    }

    public int updateAndScale(double target) {

        if (target > currentValue) {
            currentValue += (target - currentValue) * attackRate;
        } else {
            currentValue += (target - currentValue) * decayRate;
        }

        // Scale to 0 - maxDMXValue
        return (int)(currentValue * maxDMXValue);
    }
}
