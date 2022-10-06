package main;

import main.analizator.LA;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

//generator leksickih analizatora
public class GLA {

    private Map<String, String> regexNameToDefMap = new LinkedHashMap<>();
    private List<String> analyzerStates = new LinkedList<>();
    private List<String> lexicalElementNames = new LinkedList<>();
    private Map<String, List<EpsilonNKA>> stateToENKAListMap = new LinkedHashMap<>();

    public static void main(String... args) throws IOException {
        InputStream in = new FileInputStream("./data/svaki_drugi_a2.lan");
        PrintStream out = System.out;

        GLA gla = new GLA();
        gla.parseInput(in, out);

        in.close();
    }

    public void parseInput(InputStream in, PrintStream out) {
        Scanner sc = new Scanner(in);

        inputRegexDefinitions(sc);

        inputAnalyzerStates(sc);

        inputLexicalElementNames(sc);

        inputAnalyzerRules(sc);
    }

    public void inputRegexDefinitions(Scanner sc){
        boolean atLeastOneDefinition = false;
        while(!sc.hasNext("%.*")){
            atLeastOneDefinition = true;
            String regexName = sc.next();
            regexName = regexName.substring(1, regexName.length() - 1);

            String regexDef = sc.next();

            //System.out.println(regexName + " -> " + regexDef);


            regexDef = RegexUtil.simplifyRegex(regexDef, regexNameToDefMap);


            //System.out.println(regexName + " -> " + regexDef);
            regexNameToDefMap.put(regexName, regexDef);
        }

        if(atLeastOneDefinition){
            sc.nextLine();
        }

        //System.out.println("---------------------------- reg definicije gotove ----------------------------");
    }

    public void inputAnalyzerStates(Scanner sc){
        String nextLine = sc.nextLine();

        analyzerStates = Arrays.stream(nextLine.split(" ")).collect(Collectors.toList());
        analyzerStates.remove(0);

        analyzerStates.forEach((state) -> stateToENKAListMap.put(state, new LinkedList<>()));

        //System.out.println(analyzerStates);
        //System.out.println("---------------------------- stanja gotova ----------------------------");
    }

    public void inputLexicalElementNames(Scanner sc){
        String nextLine = sc.nextLine();

        lexicalElementNames = Arrays.stream(nextLine.split(" ")).collect(Collectors.toList());
        lexicalElementNames.remove(0);

        //System.out.println(lexicalElementNames);
        //System.out.println("---------------------------- imena leksičkih jedinki gotova ----------------------------");
    }

    public void inputAnalyzerRules(Scanner sc){
        while(sc.hasNext()){
            String firstLine = sc.nextLine();

            String stateName = firstLine.substring(1, firstLine.indexOf(">"));
            String regexDef = firstLine.substring(firstLine.indexOf(">") + 1);
            regexDef = RegexUtil.simplifyRegex(regexDef, regexNameToDefMap);

            //System.out.print(stateName + " " + regexDef + " ---> ");

            sc.nextLine(); // {
            String lexicalElementName = sc.nextLine();
            //System.out.print(" [" + lexicalElementName + "] ");

            List<Action> actions = new LinkedList<>();
            while(!sc.hasNext("}.*")) {
                String actionString = sc.nextLine();

                Action action = new Action(ActionType.valueOf(actionString.split(" ")[0]));

                if(actionString.split(" ").length > 1){
                    action.setActionArgument(actionString.split(" ")[1]);
                }

                actions.add(action);

                //System.out.print(action.getActionType() + " " + action.getActionArgument() + ", ");
            }
            //System.out.println();

            sc.nextLine(); // }

            EpsilonNKA eNKA = new EpsilonNKA();
            Pair<Integer, Integer> result = RegexUtil.buildENKAFromRegex(regexDef, eNKA);
            eNKA.setFirstState(result.getFirst());
            eNKA.setAcceptableState(result.getSecond());
            eNKA.setLexicalElementName(lexicalElementName);
            eNKA.setActions(actions);

            stateToENKAListMap.get(stateName).add(eNKA);
        }
        //System.out.println("---------------------------- pravila gotova ----------------------------");
    }

    public Map<String, String> getRegexNameToDefMap() {
        return regexNameToDefMap;
    }

    public List<String> getAnalyzerStates() {
        return analyzerStates;
    }

    public List<String> getLexicalElementNames() {
        return lexicalElementNames;
    }

    public Map<String, List<EpsilonNKA>> getStateToENKAListMap() {
        return stateToENKAListMap;
    }
}