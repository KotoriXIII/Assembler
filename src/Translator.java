import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Translator {

    /**
     * The {@link Translator#lines} variable will be used to track the lines of the file.
     */
    private LinkedList<String> lines = new LinkedList<>();
    /**
     * The {@link Translator#labels} variable will be used to track the key:label_name in String and value:line_number in Integer
     */
    private HashMap<String, Integer> labels = new HashMap<>();
    /**
     * The {@link Translator#variables} variable will be used to track the key:variable_name in String and value:line_number in Integer
     */
    private HashMap<String, Integer> variables = new HashMap<>();

    /**
     * The identifier for a valid name.
     */
    private final String VALID_IDENTIFIER = "^[A-Za-z.$_0-9]*$";

    /**
     * debugging
     */
    private File file;


    /**
     * Transforms an assembly file into machine code.
     *
     * @param file The assembly file
     */
    public Translator(File file) {

        this.file = file;

        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNext()) {

                String rawLine = sc.nextLine();
                String cleanedLine = clean(rawLine);
                if (!cleanedLine.isEmpty())
                    lines.add(cleanedLine);
            }

            initLabels();
            initVariables();

            sc.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Converts variables and labels to values.
     */
    public void map() {
        ListIterator<String> li = this.lines.listIterator(); //iterator

        int labelCounter = 0;
        for(int i = 0; li.hasNext(); i++)
        {
            final String OFFICIAL_LINE = li.next(); //grab the line
            String line = OFFICIAL_LINE;

            if (line.startsWith("(") && line.endsWith(")")) //candidate for a label
            {
                line = line.substring(1, line.length() - 1); //isolate the name of the label

                if (labels.containsKey(line)) {
                    this.labels.put(line, i-labelCounter);
                    li.remove();
                    labelCounter++;
                }
            }
        }

       li = this.lines.listIterator(); //iterator


        for (int i = 0; li.hasNext(); i++)//loop through every single line in the list of lines
        {
            final String OFFICIAL_LINE = li.next(); //grab the line
            String line = OFFICIAL_LINE;

            if (line.startsWith("@")) //candidate for a var
            {
                line = line.substring(1); //isolate the name of the var
                if (variables.containsKey(line)) {
                   li.set("@" + variables.get(line).intValue());
                } else if (labels.containsKey(line))
                {
                    li.set("@" + labels.get(line).intValue());
                }
            } else if (line.startsWith("(") && line.endsWith(")")) //candidate for a label
            {
                System.out.println("ERROR HOW THIS HAPPEN?? MAP fUNCTION");
            }


        }
    }

    public void saveToCleanAssembly(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ListIterator<String> li = lines.listIterator();

        while (li.hasNext()) {
            writer.println(li.next());
        }

        writer.close();
    }

    public void saveToBinary(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Parser parser = new Parser(this.lines);
        parser.parse();
        ListIterator<String> li = parser.getParsedLines().listIterator();

        while (li.hasNext()) {
            writer.println(li.next());
        }

        writer.close();
    }


    private void initVariables() {


        this.variables.put("SCREEN", new Integer(16384));

        ListIterator<String> li = this.lines.listIterator(); //iterator

        int varCounter = 16;
        while (li.hasNext())//loop through every single line in the list of lines
        {
            final String OFFICIAL_LINE = li.next(); //grab the line
            String line = OFFICIAL_LINE;

            if (line.startsWith("@")) //candidate for a var
            {
                line = line.substring(1); //isolate the name of the var

                if(line.matches("[0-9]+")){
                    continue;
                }

                if (isRegisterNotation(line)) {
                    this.lines.set(this.lines.indexOf(OFFICIAL_LINE), getRegisterStringValue(line));
                    continue;
                }


                if (isValidNamingConvention(line)) { //check if has valid name regex
                    if (!labels.containsKey(line)) //if it has already been found as a label then SKIP!
                    {
                        if(!variables.containsKey(line)) {
                            System.out.println(varCounter + " for variable " + line);
                            variables.put(line, varCounter++);
                        }
                    }

                } else {
                    throw new RuntimeException("The variable : \"" + line + "\" does not have a valid name structure.");
                }

            }
        }
    }

    /**
     * Initializes {@link Translator#labels}} with its corresponding variable name and line number it is found in.
     */
    private void initLabels() {


        ListIterator<String> li = this.lines.listIterator(); //iterator

        for (int i = 0; li.hasNext(); i++) //loop through every single line in the list of lines
        {
            String line = li.next(); //grab the line
            if (line.startsWith("(") && line.endsWith(")")) //candidate for a label
            {
                line = line.substring(1, line.length() - 1); //isolate the name of the label

                if (isValidNamingConvention(line)) { //check if has valid name regex
                    if (labels.containsKey(line)) {
                        throw new RuntimeException("The label : \"" + line + "\" has already been declared.");
                    }
                    labels.put(line, i);


                } else {
                    throw new RuntimeException("The label : \"" + line + "\" does not have a valid name structure. This exception occurred in file: " + file.getName());
                }

            }
        }
    }

    /**
     * Returns the actual string value of register notation.
     *
     * @param line The string line that will be checked
     * @return @VALUE in String
     */
    private String getRegisterStringValue(String line) {
        if (line.startsWith("R")) {
            line = line.substring(1);
            try {
                int i = Integer.parseInt(line);
                if (i >= 0 && i <= 15) //todo might need to remove this. is this a constraint? if removed also fix isRegisterNotation
                {
                    return "@" + i;
                }
            } catch (NumberFormatException e) {
                return null;
            }

        }
        return null;

    }

    /**
     * Checks if the line is registered notation. For instance, @R15 is register notation whereas @sum is not. this is a variable.
     *
     * @param line The string line that will be checked
     * @return true if notation false otherwise
     */
    private boolean isRegisterNotation(String line) {
        if (line.startsWith("R")) {
            line = line.substring(1);
            try {
                int i = Integer.parseInt(line);
                if (i >= 0 && i <= 15) {
                    return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }

        }
        return false;
    }


    /**
     * Checks if the line is valid for naming conventions
     *
     * @param line The line being matched
     * @return true if is valid false otherwise
     */
    private boolean isValidNamingConvention(String line) {

        if (Character.isDigit(line.charAt(0))) //starts with number
            return false;

        if (line.matches(VALID_IDENTIFIER))
            return true;

        return false;
    }

    /**
     * Removes comments and extra whitespace characters.
     *
     * @param line The string line that will be cleaned
     * @return The clean line
     */
    private String clean(String line) {

        int index = indexOfComments(line);

        if (index != -1) {
            line = line.substring(0, index);
        }

        return line.trim();

    }

    /**
     * Gets the first index of '//'.
     *
     * @param line The string line that we are checking for
     * @return the index where the first backslash is found (inclusive)
     */
    private int indexOfComments(String line) {
        if (line.length() < 2) {
            return -1;
        }

        char[] arr = line.toCharArray();
        char val = arr[0];

        for (int i = 1; i < arr.length; i++) {
            if (val == '/' && arr[i] == '/') {
                return i - 1;
            }

            val = arr[i];
        }

        return -1;
    }
}
