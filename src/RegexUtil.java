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
}
