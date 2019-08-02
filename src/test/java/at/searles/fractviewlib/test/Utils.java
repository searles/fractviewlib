package at.searles.fractviewlib.test;

import at.searles.fractviewlib.gson.Serializers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static String readResourceFile(String filename) throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        return readFile(new File("src/test/resources/" + filename));
    }

    public static <A> A parse(String json, Class<A> cl) {
        return Serializers.serializer().fromJson(json, cl);
    }

    public static String readFile(File file) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }

            return sb.toString();
        }
    }
}
