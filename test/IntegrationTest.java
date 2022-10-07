import main.GLA;
import main.analizator.LA;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IntegrationTest {

    @ParameterizedTest
    @MethodSource("provideTestTitles")
    //@ValueSource(strings = {"nadji_a2"})
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

    private static Stream<Arguments> provideTestTitles() throws IOException {
        try (Stream<Path> walk = Files.walk(Path.of("./test-data/"))) {
            List<String> result;
            result = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.getFileName().toString())
                    .filter(f -> f.endsWith(".lan"))
                    .map(s -> s.substring(0, s.lastIndexOf(".")))
                    .collect(Collectors.toList());

            return result.stream().map(Arguments::of);
        }
    }

    public static String normalizeString(String s){
        return s.replace("\r\n", "\n")
                .replace("\r", "\n").trim();
    }
}
