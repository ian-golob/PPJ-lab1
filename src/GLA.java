import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

//generator leksickih analizatora
public class GLA {

    Map<String, String> regexNameToDefMap;
    List<String> analyzerStates;
    List<String> lexicalElementNames;
    Map<String, List<EpsilonNKA>> stateToENKAMap;

    public static void main(String... args) throws FileNotFoundException {
        parseInput(new FileInputStream("./data/minusLang.txt"), System.out);
    }

    public static void parseInput(InputStream in, PrintStream out){
        Scanner sc = new Scanner(in);
        GLA gla = new GLA();

        gla.inputRegexDefinitions(sc);

        gla.inputAnalyzerStates(sc);

        gla.inputLexicalElementNames(sc);

        gla.inputAnalyzerRules(sc);
    }

    public void inputRegexDefinitions(Scanner sc){
        regexNameToDefMap = new HashMap<>();

        while(!sc.hasNext("%.*")){
            String regexName = sc.next();
            regexName = regexName.substring(1, regexName.length() - 1);

            String regexDef = sc.next();

            System.out.println(regexName + " -> " + regexDef);


            regexDef = RegexUtil.simplifyRegex(regexDef, regexNameToDefMap);


            System.out.println(regexName + " -> " + regexDef);
            regexNameToDefMap.put(regexName, regexDef);
        }

        sc.nextLine();
        System.out.println("---------------------------- reg definicije gotove ----------------------------");
    }

    public void inputAnalyzerStates(Scanner sc){
        String nextLine = sc.nextLine();

        analyzerStates = Arrays.stream(nextLine.split(" ")).collect(Collectors.toList());
        analyzerStates.remove(0);

        System.out.println(nextLine);
        System.out.println(analyzerStates);
        System.out.println("---------------------------- stanja gotova ----------------------------");
    }

    public void inputLexicalElementNames(Scanner sc){
        String nextLine = sc.nextLine();

        lexicalElementNames = Arrays.stream(nextLine.split(" ")).collect(Collectors.toList());
        lexicalElementNames.remove(0);

        System.out.println(nextLine);
        System.out.println(lexicalElementNames);
        System.out.println("---------------------------- imena leksičkih jedinki gotova ----------------------------");
    }

    public void inputAnalyzerRules(Scanner sc){
        while(sc.hasNext()){
            String firstLine = sc.nextLine();

            String stateName = firstLine.substring(1, firstLine.indexOf(">"));
            String regexDef = firstLine.substring(firstLine.indexOf(">") + 1);
            regexDef = RegexUtil.simplifyRegex(regexDef, regexNameToDefMap);

            System.out.print(stateName + " " + regexDef + " ---> ");

            sc.nextLine(); // {

            while(!sc.hasNext("}.*")) {
                System.out.print(sc.nextLine() + ", ");
            }
            System.out.println();

            sc.nextLine(); // }
        }
        System.out.println("---------------------------- pravila gotova ----------------------------");
    }
}
