package com.deuceng.toc;

public class Main {

    public static void main(String[] args) {
        fsm2regex f2r = new fsm2regex();
        f2r.readFile();
        f2r.convertToGeneralizedNFA();
    }
}
