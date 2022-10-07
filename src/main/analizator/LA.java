package main.analizator;

import main.Action;
import main.EpsilonNKA;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

//leksicki analizator
public class LA {

    private String currentState;

    public static void main(String... args){

    }

    private final List<String> analyzerStates;
    private final List<String> lexicalElementNames;
    private final Map<String, List<EpsilonNKA>> stateToENKAListMap;

    public LA(List<String> analyzerStates, List<String> lexicalElementNames, Map<String, List<EpsilonNKA>> stateToENKAListMap) {
        this.analyzerStates = analyzerStates;
        this.lexicalElementNames = lexicalElementNames;
        this.stateToENKAListMap = stateToENKAListMap;
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
