package com.deuceng.toc;

import java.util.Stack;

public class Transition {
    private State source;
    private State dest;
    private String label;

    public Transition(State source, State dest, String label) {
        this.source = source;
        this.dest = dest;
        this.label = label;
    }


    public State getSource() {
        return source;
    }

    public void setSource(State source) {
        this.source = source;
    }

    public State getDest() {
        return dest;
    }

    public void setDest(State dest) {
        this.dest = dest;
    }

    public String getLabel() {
        if (label.length() == 0) return Symbol.EPSILON;
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return "Transition{" +
                "source=" + source +
                ", dest=" + dest +
                ", label='" + label + '\'' +
                '}';
    }
}
