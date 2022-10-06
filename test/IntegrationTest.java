import main.GLA;
import main.analizator.LA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IntegrationTest {

    @ParameterizedTest
    @ValueSource(strings = {"minusLang", "nadji_a1", "nadji_a2",
            "simplePpjLang", "svaki_drugi_a1", "svaki_drugi_a2"})
    public void integrationTest(String title) throws IOException {
        String lanFileName = "./test-data/" + title + ".lan";
        String inFileName = "./test-data/" + title + ".in";
        String outFileName = "./test-data/" + title + ".out";
        String myFileName = "./test-data/" + title + ".my";

        //run generator
        GLA gla = new GLA();
        try(InputStream in = new FileInputStream(lanFileName)){
            PrintStream out = new PrintStream(PrintStream.nullOutputStream());
            gla.parseInput(in, out);
        }

        //run analyzer
        LA la = new LA(gla.getAnalyzerStates(), gla.getLexicalElementNames(), gla.getStateToENKAListMap());

        try(InputStream input = new FileInputStream(inFileName);
            PrintStream output = new PrintStream(new FileOutputStream(myFileName))){
            la.analyzeInput(input, output);
        }

        String myOutput = Files.readString(Path.of(myFileName));
        String correctOutput = Files.readString(Path.of(outFileName));

        assertEquals(normalizeString(correctOutput), normalizeString(myOutput));
    }

    public static String normalizeString(String s){
        return s.replace("\r\n", "\n")
                .replace("\r", "\n").trim();
    }
}
