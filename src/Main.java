import java.io.File;

public class Main {

    public static final String USER_DIR_PATH =  System.getProperty("user.dir");
    public static final String RESOURCES_DIR_PATH = USER_DIR_PATH + "/resources";
    public static final String IN_DIR_PATH = RESOURCES_DIR_PATH + "/in";
    public static final String OUT_DIR_PATH = RESOURCES_DIR_PATH + "/out";

    public static final File IN_DIR = new File(IN_DIR_PATH);
    public static final File OUT_DIR = new File(OUT_DIR_PATH);



    public static void main(String[] args) {


        File[] inDirArr = IN_DIR.listFiles();

        for(File f : inDirArr)
        {
            Translator t = new Translator(f);
            t.map();
            t.saveToCleanAssembly(OUT_DIR + "/" + f.getName().split("[.]")[0] + "_ASSEMBLY.asm");
            t.saveToBinary(OUT_DIR + "/" + f.getName().split("[.]")[0] + "_BINARY.hack");

        }


    }
}
