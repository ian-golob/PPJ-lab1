import java.util.*;

public class EpsilonNKA {
    private Integer firstState;
    private Integer acceptableState;
    private Integer stateCount;
    private Map<Pair<Integer, Character>,Set<Integer>> transitions;
    private Map<Integer,Set<Integer>> epsilonTransitions;

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
    }

    public void resetState(){
        currentState.clear();
        currentState.add(firstState);
        epsilonTransition();
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

}
