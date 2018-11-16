import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class keywordcounter {

    public static void main(String[] args) throws Exception {
        /* Relative path to the output file */
        String path = "output_file.txt";
        File file = new File(path);
        try{
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        /* BufferedWriter for writing into the file */
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        InputOutputParser p = new InputOutputParser();

        p.readFile(bw,args[0]);
    }
}
