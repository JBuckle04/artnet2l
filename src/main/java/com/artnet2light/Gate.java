package com.artnet2light;

public class Gate {
    private final double threshold;
    private double previous = 0;

    public Gate(double threshold) {
        this.threshold = threshold;
    }

    public double tryGate(double value) {
        boolean allow = value >= threshold || value < previous;
        previous = value;
        return allow ? value : 0;
    }
}
