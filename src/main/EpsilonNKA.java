package main;

import java.util.*;

public class EpsilonNKA {
    private Integer firstState;
    private Integer acceptableState;
    private Integer stateCount;
    private Map<Pair<Integer, Character>,Set<Integer>> transitions;
    private Map<Integer,Set<Integer>> epsilonTransitions;
    private String lexicalElementName;
    private List<Action> actions;

    private Set<Integer> currentState;

    public Integer addNewState(){
        return stateCount++;
    }

    public void addEpsilonTransition(Integer fromState, Integer toState){
        epsilonTransitions.put(fromState, epsilonTransitions.getOrDefault(fromState, new HashSet<>()));

        epsilonTransitions.get(fromState).add(toState);
    }

    public void addTransition(Integer fromState,Character inputCharacter, Integer toState){
        Pair<Integer, Character> input = new Pair<>(fromState, inputCharacter);
        transitions.put(input, transitions.getOrDefault(input, new HashSet<>()));

        transitions.get(input).add(toState);
    }

    public EpsilonNKA() {
        stateCount = 0;
        currentState = new TreeSet<>();
        transitions = new HashMap<>();
        epsilonTransitions = new HashMap<>();
        actions = new LinkedList<>();
    }

    public void resetState(){
        currentState.clear();
        currentState.add(firstState);
        epsilonTransition();
    }

    public boolean inAcceptableState(){
        return currentState.contains(acceptableState);
    }

    public void goToNextState(Character inputSymbol){
        Set<Integer> nextState = new TreeSet<>();
        if(currentState.size()>0){
            for(Integer i:currentState){
                if(transitions.containsKey(new Pair<>(i,inputSymbol))){
                    nextState.addAll(transitions.get(new Pair<>(i,inputSymbol)));
                }
            }
        }
        currentState = nextState;

        epsilonTransition();
    }

    private void epsilonTransition(){
        if(currentState.size()>0){
            int stateNum;
            do{
                Set<Integer> epsilonState = new TreeSet<>();
                stateNum = currentState.size();
                for(Integer i:currentState){
                    if(epsilonTransitions.containsKey(i)) {
                        epsilonState.addAll(epsilonTransitions.get(i));
                    }
                }
                currentState.addAll(epsilonState);
            }while(currentState.size()!=stateNum);
        }
    }

    public Integer getFirstState() {
        return firstState;
    }

    public void setFirstState(Integer firstState) {
        this.firstState = firstState;
    }

    public Integer getAcceptableState() {
        return acceptableState;
    }

    public void setAcceptableState(Integer acceptableState) {
        this.acceptableState = acceptableState;
    }

    public Integer getStateCount() {
        return stateCount;
    }

    public void setStateCount(Integer stateCount) {
        this.stateCount = stateCount;
    }

    public Map<Pair<Integer, Character>, Set<Integer>> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<Pair<Integer, Character>, Set<Integer>> transitions) {
        this.transitions = transitions;
    }

    public Map<Integer, Set<Integer>> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    public void setEpsilonTransitions(Map<Integer, Set<Integer>> epsilonTransitions) {
        this.epsilonTransitions = epsilonTransitions;
    }

    public Set<Integer> getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Set<Integer> currentState) {
        this.currentState = currentState;
    }

    public String getLexicalElementName() {
        return lexicalElementName;
    }

    public void setLexicalElementName(String lexicalElementName) {
        this.lexicalElementName = lexicalElementName;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
