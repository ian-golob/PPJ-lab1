import java.io.*;
import java.util.*;

//leksicki analizator
public class LA {

    private String currentState;
    private List<String> analyzerStates;
    private List<String> lexicalElementNames;
    private Map<String, List<EpsilonNKA>> stateToENKAListMap;

    public LA(){

    }

    public LA(List<String> analyzerStates, List<String> lexicalElementNames, Map<String, List<EpsilonNKA>> stateToENKAListMap) {
        this.analyzerStates = analyzerStates;
        this.lexicalElementNames = lexicalElementNames;
        this.stateToENKAListMap = stateToENKAListMap;
    }

    public static void main(String... args) throws IOException, ClassNotFoundException {
        LA la = new LA();
        la.readLAConfigObjects();
        la.analyzeInput(System.in, System.out);
    }

    @SuppressWarnings("unchecked")
    public void readLAConfigObjects() throws IOException, ClassNotFoundException {
        String pathPrefix = "";

        String analyzerStatesPath = pathPrefix + "analyzerStates.obj";
        String lexicalElementNamesPath = pathPrefix + "lexicalElementNames.obj";
        String stateToENKAListMapPath = pathPrefix + "stateToENKAListMap.obj";

        try(ObjectInputStream analyzerStatesIn = new ObjectInputStream(new FileInputStream(analyzerStatesPath));
            ObjectInputStream lexicalElementNamesIn = new ObjectInputStream(new FileInputStream(lexicalElementNamesPath));
            ObjectInputStream stateToENKAListMapIn = new ObjectInputStream(new FileInputStream(stateToENKAListMapPath))){
            analyzerStates = (List<String>) analyzerStatesIn.readObject();
            lexicalElementNames = (List<String>) lexicalElementNamesIn.readObject();
            stateToENKAListMap = (Map<String, List<EpsilonNKA>>) stateToENKAListMapIn.readObject();
        }
    }

    public void analyzeInput(InputStream in, PrintStream out) throws IOException {
        StringBuilder inputBuilder = new StringBuilder();
        int nextChar;
        while((nextChar = in.read()) != -1){
            char c = (char) nextChar;
            inputBuilder.append(c);
        }
        String input = inputBuilder.toString()
                    .replace("\r\n", "\n")
                    .replace("\r", "\n");

        currentState = analyzerStates.get(0);

        int lastNonParsedCharacterAt = 0;
        int newLineCount = 1;
        while(lastNonParsedCharacterAt < input.length()){

            List<EpsilonNKA> eNKAs = stateToENKAListMap.get(currentState);
            eNKAs.forEach(EpsilonNKA::resetState);

            boolean foundAnyMatch = false;
            int lastMatchFoundAt = -1;
            int eNKAMatchedId = -1;
            for(int currentPosition = lastNonParsedCharacterAt; currentPosition < input.length(); currentPosition++){
                boolean foundNewMatch = false;
                for(int eNKAId = 0; eNKAId < eNKAs.size(); eNKAId++){
                    EpsilonNKA eNKA = eNKAs.get(eNKAId);
                    eNKA.goToNextState(input.charAt(currentPosition));
                    if(eNKA.inAcceptableState() && !foundNewMatch){
                        foundNewMatch = true;
                        foundAnyMatch = true;
                        lastMatchFoundAt = currentPosition;
                        eNKAMatchedId = eNKAId;
                    }
                }
            }

            if(foundAnyMatch){
                EpsilonNKA eNKA = eNKAs.get(eNKAMatchedId);

                int oldNewLineCount = newLineCount;
                List<Action> actions = eNKA.getActions();
                for(Action action: actions){
                    switch (action.getActionType()){
                        case NOVI_REDAK:
                            newLineCount++;
                            break;
                        case UDJI_U_STANJE:
                            currentState = action.getActionArgument();
                            break;
                        case VRATI_SE:
                            lastMatchFoundAt = lastNonParsedCharacterAt + Integer.parseInt(action.getActionArgument()) - 1;
                            break;
                    }
                }
                if(!eNKA.getLexicalElementName().equals("-")){
                    out.println(eNKA.getLexicalElementName() + " " + oldNewLineCount + " " + input.substring(lastNonParsedCharacterAt, lastMatchFoundAt + 1));
                }

                lastNonParsedCharacterAt = lastMatchFoundAt + 1;
            }else{
                System.err.print(input.charAt(lastNonParsedCharacterAt));
                lastNonParsedCharacterAt++;
            }
        }

    }
}
