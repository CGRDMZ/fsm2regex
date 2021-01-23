package com.deuceng.toc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class fsm2regex {
    public static final String EMPTY = "\u00F8";
    public static final String LAMBDA_DISPLAY = "\u03BB";

    public static final String LAMBDA = "";

    /* the string for the kleene star. */
    public static final String KLEENE_STAR = "*";

    /* the string for the or symbol. */
    public static final String OR = "+";

    /**
     * right paren.
     */
    public static final String RIGHT_PAREN = ")";

    /**
     * left paren.
     */
    public static final String LEFT_PAREN = "(";
    private FiniteStateMachine fsm;

    public fsm2regex() {
        fsm = new FiniteStateMachine();
    }

    public void readFile() {
        try {
            Scanner sc = new Scanner(new File("./dfa.txt"));
            String initialState = sc.nextLine().split("=")[1];
            String[] finalStatesTokens = sc.nextLine().split("=")[1].split(",");

            // ignore the alphabet
            sc.nextLine();

            String[] states = sc.nextLine().split("=")[1].split(",");

            // add states
            for (String state :
                    states) {
                addState(state);
            }

            setInitialState(initialState);



            setFinalStates(finalStatesTokens);


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

    public void convertToGeneralizedNFA() {

        fsm.addState(new State("start"));
        fsm.addTransition(new Transition(fsm.getState("start"), fsm.getInitialState(), LAMBDA));
        setInitialState("start");

        fsm.addState(new State("final"));

        for (State finalState :
                fsm.getFinalStates()) {
            fsm.addTransition(new Transition(finalState, fsm.getState("final"), LAMBDA));
        }

        setFinalStates(new String[] {"final"});
        createEmptyTransitions();

        State initialState = fsm.getInitialState();
        State finalState = fsm.getFinalStates()[0];
        State[] states = fsm.getStates();
        for (State state :
                states) {
            if (state != finalState && state != initialState) {
                Transition[] transitionsToBeRemoved = getTransitionsOfTheStateToBeRemoved(state);
                removeState(state, transitionsToBeRemoved);
            }
        }
    }

    private void removeState(State state, Transition[] transitionsToBeRemoved) {
    }

    private Transition[] getTransitionsOfTheStateToBeRemoved(State rip) {
        State[] states = fsm.getStates();
        ArrayList list = new ArrayList();
        for (State s1 :
                states) {
            if (s1 != rip) {
                for (State s2 :
                        states) {
                    if (s2 != rip) {
                        String expression = getExpression(s1, s2, rip);
                        list.add(getTransitionForExpression(s1, s2, expression));
                    }
                }
            }
        }
        return (Transition[]) list.toArray(new Transition[list.size()]);
    }

    private String getExpression(State src, State dest, State rip) {
        String betweenSrcDest = getExpressionBetweenStates(src, dest);
        String betweenSrcRip = getExpressionBetweenStates(src, rip);
        String betweenRipRip = getExpressionBetweenStates(rip, rip);
        String betweenRipDest = getExpressionBetweenStates(rip, dest);

        return or(betweenSrcDest, concatenate(concatenate(betweenSrcRip, star(betweenRipRip)), betweenRipDest));


    }

    private Transition getTransitionForExpression(State src, State dest, String exp) {
        return new Transition(src, dest, exp);
    }

    private String concatenate(String first, String second) {
        if (first.equals(EMPTY) || second.equals(EMPTY))
            return EMPTY;
        else if (first.equals(LAMBDA))
            return second;
        else if (second.equals(LAMBDA))
            return first;
        if (orTokenizer(first).length > 1)
            first = LEFT_PAREN + first + RIGHT_PAREN;
        if (orTokenizer(second).length > 1)
            second = LEFT_PAREN + second + RIGHT_PAREN;
        return first + second;
    }

    private String star(String s) {
        if (s.equals(EMPTY) || s.equals(LAMBDA)) return LAMBDA;
        if (orTokenizer(s).length > 1 || catTokenizer(s).length > 1) {
            s = LEFT_PAREN + s + RIGHT_PAREN;
        } else {
            if (s.endsWith(KLEENE_STAR))
                return s;
        }
        return s + KLEENE_STAR;
    }

    private String or(String first, String second) {
        if (first.equals(EMPTY)) return second;
        if (second.equals(EMPTY)) return first;
        if (first.equals(LAMBDA) && second.equals(LAMBDA)) return LAMBDA;
        if (first.equals(LAMBDA)) first = LAMBDA_DISPLAY;
        if (second.equals(LAMBDA)) second = LAMBDA_DISPLAY;
        return first + OR + second;
    }

    private String[] orTokenizer(String exp) {
        ArrayList se = new ArrayList(); // Subexpressions.
        int start = 0;
        int level = 0;
        for (int i = 0; i < exp.length(); i++) {
            if (exp.charAt(i) == '(')
                level++;
            if (exp.charAt(i) == ')')
                level--;
            if (exp.charAt(i) != '+')
                continue;
            if (level != 0)
                continue;
            // First level or!
            se.add(replaceIfLambda(exp.substring(start, i)));
            start = i + 1;
        }
        se.add(replaceIfLambda(exp.substring(start)));
        return (String[]) se.toArray(new String[0]);
    }

    private String[] catTokenizer(String exp) {
        ArrayList se = new ArrayList(); // Subexpressions.
        int start = 0;
        int level = 0;
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (c == ')') {
                level--;
                continue;
            }
            if (c == '(')
                level++;
            if (!(c == '(' && level == 1) && level != 0)
                continue;
            if (c == '+') {
                // Hum. That shouldn't be...
                throw new IllegalArgumentException(
                        "+ encountered in cat discretization!");
            }
            if (c == '*')
                continue;
            // Not an operator, and on the first level!
            if (i == 0)
                continue;
            se.add(replaceIfLambda(exp.substring(start, i)));
            start = i;
        }
        se.add(replaceIfLambda(exp.substring(start)));
        return (String[]) se.toArray(new String[0]);
    }

    private String replaceIfLambda(String s) {
        if (s.equals(EMPTY)) return "";
        return s;
    }

    private String getExpressionBetweenStates(State src, State dest) {
        Transition[] transitions = fsm.getTransitionsBetweenStates(src, dest);
        return transitions[0].getLabel();
    }

    private void createEmptyTransitions() {
        State[] s = fsm.getStates();
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s.length; j++)
                if (fsm.getTransitionsBetweenStates(s[i], s[j]).length == 0) {
                    Transition t = new Transition(s[i], s[j], "");
                    fsm.addTransition(t);
                }
    }

    private void setInitialState(String name) {
        fsm.setInitialState(fsm.getState(name));
    }

    private void setFinalStates(String[] finalStates) {
        State[] states = new State[finalStates.length];
        for (int i = 0; i < finalStates.length; i++) {
            states[i] = fsm.getState(finalStates[i]);
        }
        Set finalStatesSet = new HashSet(Arrays.asList(states));
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
