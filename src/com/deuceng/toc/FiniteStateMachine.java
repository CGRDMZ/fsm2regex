package com.deuceng.toc;


import java.util.*;

public class FiniteStateMachine<T> {
    protected Set states;
    protected Set finalStates;
    protected State initialState;
    protected Set transitions;
    private HashMap transitionFromStateMap;
    private HashMap transitionToStateMap;
    private HashMap transitionArrayFromStateMap;
    private HashMap transitionArrayToStateMap;

    public FiniteStateMachine() {
        states = new HashSet();
        transitions = new HashSet();
        finalStates = new HashSet();
        initialState = null;
        transitionFromStateMap = new HashMap();
        transitionToStateMap = new HashMap();
        transitionArrayFromStateMap = new HashMap();
        transitionArrayToStateMap = new HashMap();
    }

    public void addState(State s) {
        if (states.contains(s)) return;
        states.add(s);
        transitionFromStateMap.put(s, new LinkedList());
        transitionToStateMap.put(s, new LinkedList());
    }

    public void addTransition(Transition t) {
        // sanity checks
        if (t == null) throw new NullPointerException("Transition should not be null.");
        if (transitions.contains(t)) return;
        if (t.getSource() == null || t.getDest() == null) return;
        if (getState(t.getSource().getName()) == null || getState(t.getDest().getName()) == null)
            throw new NullPointerException("cant add transition to non existing state.");
        transitions.add(t);
        List list = (List) transitionFromStateMap.get(t.getSource());
        list.add(t);
        list = (List) transitionToStateMap.get(t.getDest());
        list.add(t);
    }

    public State getState(String stateName) {
        Iterator it = states.iterator();
        while (it.hasNext()) {
            State state = (State) it.next();
            if (state.getName().equals(stateName))
                return state;
        }
        return null;
    }

    public Set getFinalStates() {
        return finalStates;
    }

    public void setFinalStates(Set finalStates) {
        this.finalStates = finalStates;
    }

    public State getInitialState() {
        return initialState;
    }

    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }
}