import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EpsilonNKA {
    private final Set<String> stateSet;
    private final Set<String> symbolSet;
    private final Set<String> acceptableStateSet ;
    private final String firstState;
    private final Map<List<String>,Set<String>> transition;
    private Set<String> currentState;
    private String stateHistory;

    public EpsilonNKA(Set<String> stateSet, Set<String> symbolSet, Set<String> acceptableStateSet, String firstState, Map<List<String>, Set<String>> transition) {
        this.stateSet = stateSet;
        this.symbolSet = symbolSet;
        this.acceptableStateSet = acceptableStateSet;
        this.firstState = firstState;
        this.transition = transition;
        currentState = new TreeSet<>();
        resetState();
    }

    public void resetState(){
        currentState.clear();
        currentState.add(firstState);
        epsilonTransition();
    }

    public void goToNextState(String inputSymbol){
        Set<String> nextState = new TreeSet<>();
        if(currentState.size()>0){
            for(String i:currentState){
                if(transition.containsKey(List.of(i,inputSymbol))){
                    nextState.addAll(transition.get(List.of(i,inputSymbol)));

                }
            }
            nextState.remove("#");  //remove empty states
        }
        currentState = nextState;

        epsilonTransition();
    }

    private void epsilonTransition(){
        if(currentState.size()>0){
            int stateNum;
            do{
                Set<String> epsilonState = new TreeSet<>();
                stateNum = currentState.size();
                for(String i:currentState){
                    if(transition.containsKey(List.of(i,"$"))) {
                        epsilonState.addAll(transition.get(List.of(i,"$")));
                    }
                }
                currentState.addAll(epsilonState);
                currentState.remove("#"); //remove empty states
            }while(currentState.size()!=stateNum);
        }
    }
}
