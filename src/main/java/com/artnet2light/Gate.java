package com.artnet2light;

public class Gate {
    private final int threshold;
    private int previous = 0;

    public Gate(int threshold) {
        this.threshold = threshold;
    }

    public int tryGate(int value) {
        boolean allow = value >= threshold || value < previous;
        previous = value;
        return allow ? value : 0;
    }
}
