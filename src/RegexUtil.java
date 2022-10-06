import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RegexUtil {
    public static String simplifyRegex(String regex, Map<String, String> regexNameToDefMap){
        for(int i = 0; i < regex.length(); i++){
            if(regex.charAt(i) == '{' && isOperator(regex, i)){
                int substringEnd = regex.indexOf('}', i);

                String regexDefSubstring = regex.substring(i + 1, substringEnd);

                regex = regex.substring(0, i)
                        + "(" + regexNameToDefMap.get(regexDefSubstring) + ")"
                        + regex.substring(substringEnd + 1);
            }
        }
        return regex;
    }

    public static boolean isOperator(String s, int position){
        boolean result = true;
        for(int i = position - 1; i >= 0; i--){
            if(s.charAt(i) == '\\'){
                result = !result;
            }else{
                return result;
            }
        }
        return result;
    }

    public static Pair<Integer, Integer> buildENKAFromRegex(String regex, EpsilonNKA eNKA){
        List<String> choices = new LinkedList<>();
        int bracketCount = 0;

        int lastChoiceSubstringEnd = 0;
        boolean choiceCharacterFound = false;
        for(int i = 0; i < regex.length(); i++){
            if(regex.charAt(i) == '(' && RegexUtil.isOperator(regex, i)){
                bracketCount++;
            }else if(regex.charAt(i) == ')' && RegexUtil.isOperator(regex, i)){
                bracketCount--;
            }else if(bracketCount == 0 && regex.charAt(i) == '|' && RegexUtil.isOperator(regex, i)) {
                choices.add(regex.substring(lastChoiceSubstringEnd, i));
                choiceCharacterFound = true;
                lastChoiceSubstringEnd = i + 1;
            }
        }

        if(choiceCharacterFound){
            choices.add(regex.substring(lastChoiceSubstringEnd));
        }

        int leftState = eNKA.addNewState();
        int rightState = eNKA.addNewState();
        if(choiceCharacterFound){
            for (String choice : choices) {
                Pair<Integer, Integer> result = buildENKAFromRegex(choice, eNKA);
                eNKA.addEpsilonTransition(leftState, result.getFirst());
                eNKA.addEpsilonTransition(result.getSecond(), rightState);
            }
        }else{
            boolean prefixed = false;
            int lastState = leftState;
            for(int i = 0; i < regex.length(); i++){
                int a,b;
                if(prefixed){
                    //case 1
                    prefixed = false;
                    char transitionalCharacter;
                    switch (regex.charAt(i)){
                        case 't':
                            transitionalCharacter = '\t';
                            break;
                        case 'n':
                            transitionalCharacter = '\n';
                            break;
                        case '_':
                            transitionalCharacter = ' ';
                            break;
                        default:
                            transitionalCharacter = regex.charAt(i);
                            break;
                    }
                    a = eNKA.addNewState();
                    b = eNKA.addNewState();
                    eNKA.addTransition(a, transitionalCharacter, b);
                }else{
                    //case 2
                    if(regex.charAt(i) == '\\'){
                        prefixed = true;
                        continue;
                    }

                    if(regex.charAt(i) != '('){
                        //case 2a
                        a = eNKA.addNewState();
                        b = eNKA.addNewState();
                        if(regex.charAt(i) == '$'){
                            eNKA.addEpsilonTransition(a, b);
                        }else{
                            eNKA.addTransition(a, regex.charAt(i), b);
                        }
                    }else{
                        //case 2b
                        int j = regex.indexOf(')', i); //TODO: fix to include actual occurrence
                        Pair<Integer, Integer> result = buildENKAFromRegex(regex.substring(i + 1, j), eNKA);
                        a = result.getFirst();
                        b = result.getSecond();
                        i = j;
                    }
                }

                //check if repeating
                if(i + 1 < regex.length() && regex.charAt(i + 1) == '*'){
                    int x = a;
                    int y = b;
                    a = eNKA.addNewState();
                    b = eNKA.addNewState();

                    eNKA.addEpsilonTransition(a, x);
                    eNKA.addEpsilonTransition(y, b);
                    eNKA.addEpsilonTransition(a, b);
                    eNKA.addEpsilonTransition(y, x);

                    i++;
                }

                //connect
                eNKA.addEpsilonTransition(lastState, a);
                lastState = b;
            }
            eNKA.addEpsilonTransition(lastState, rightState);
        }
        return new Pair<>(leftState, rightState);
    }
}
