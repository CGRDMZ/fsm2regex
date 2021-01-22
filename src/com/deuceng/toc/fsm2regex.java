package com.deuceng.toc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class fsm2regex {
    private FiniteStateMachine fsm;

    public fsm2regex() {
        fsm = new FiniteStateMachine();
    }

    public void readFile() {
        try {
            Scanner sc = new Scanner(new File("./dfa.txt"));
            String initialState = sc.nextLine().split("=")[1];
            String[] finalStates = sc.nextLine().split("=")[1].split(",");

            // ignore the alphabet
            sc.nextLine();

            String[] states = sc.nextLine().split("=")[1].split(",");

            // add states
            for (String state :
                    states) {
                addState(state);
            }

            setInitialState(initialState);

            setFinalStates(finalStates);


            // add transitions
            while (sc.hasNextLine()) {
                String transitionLine = sc.nextLine();
                addTransition(transitionLine.split(",")[0], transitionLine.split("=")[1], transitionLine.split("=")[0].split(",")[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addTransition(String from, String to, String label) {
        State fromState = fsm.getState(from);
        State toState = fsm.getState(to);
        fsm.addTransition(new Transition(fromState, toState, label));
    }

    private void addState(String name) {
        fsm.addState(new State(name));
    }

    private void convertToGeneralizedNFA() {
        State initialState = fsm.getInitialState();
        State finalState = fsm.getFinalStates()[0];
        State[] states = fsm.getStates();
        for (State state :
                states) {
            if (state != finalState && state != initialState) {
                getTransitionsOfTheStateToBeRemoved(state);
            }
        }
    }

    private void setInitialState(String name) {
        fsm.setInitialState(fsm.getState(name));
    }

    private void setFinalStates(String[] finalStates) {
        Set finalStatesSet = new HashSet(Arrays.asList(finalStates));
        fsm.setFinalStates(finalStatesSet);
    }

//    private void readInputFile() {
//        fsm = new FiniteStateMachine<String>();
//        try {
//            Scanner sc = new Scanner(new File("./dfa.txt"));
//            initialState = sc.nextLine().split("=")[1];
//            System.out.println("initial state=" + initialState);
//            String[] acceptingStates = sc.nextLine().split("=")[1].split(",");
//            System.out.println(acceptingState);
//            alphabet.addAll(Arrays.asList(sc.nextLine().split("=")[1].split(",")));
//            alphabet.add("E");
//            System.out.println(alphabet);
//            String[] states = sc.nextLine().split("=")[1].split(",");
//            System.out.println(Arrays.toString(states));
//            L = new String[states.length + 2][states.length + 2];
//            for (String[] row :
//                    L) {
//                Arrays.fill(row, "");
//            }
//            System.out.println(L);
//            int index = 0;
//            this.state.put("start", index);
//            index++;
//            for (String state :
//                    states) {
//                this.state.put(state, index);
//                index++;
//            }
//            while (sc.hasNextLine()) {
//                String line = sc.nextLine();
//                String[] parsedLine = line.trim().split("=");
//                String src = parsedLine[0].split(",")[0];
//                String dest = parsedLine[1];
//                String label = parsedLine[0].split(",")[1];
////                fsm.addTransition(src, dest, label);
//                L[state.get(src)][state.get(dest)] = label;
//            }
//
//
//            this.state.put("end", index);
//
////            fsm.addTransition("start", initialState, "E");
//            L[state.get("start")][state.get(initialState)] = "E";
//            initialState = "start";
//            if (acceptingState.length() != 1) {
//                for (String state :
//                        acceptingStates) {
////                    fsm.addTransition(state, "end", "E");
//                    L[this.state.get(state)][this.state.get("end")] = "E";
//                }
//            }
//            acceptingState = "end";
//            System.out.println(fsm);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return;
//    }


}
