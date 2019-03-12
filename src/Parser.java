import java.util.*;

public class Parser{

    private LinkedList<String> lines;

    private HashMap<String, String> compCodes = new HashMap<>();
    private HashMap<String, String> destCodes = new HashMap<>();
    private HashMap<String, String> jumpCodes = new HashMap<>();

    public Parser(LinkedList<String> list) {
        initCompCodes();
        initDestCodes();
        initJumpCodes();

        this.lines = list;
    }

    public void initCompCodes() {
        compCodes.put("0", "101010");
        compCodes.put("1", "111111");
        compCodes.put("-1", "111010");
        compCodes.put("D", "001100");
        compCodes.put("A", "110000");
        compCodes.put("M", "110000");
        compCodes.put("!D", "001101");
        compCodes.put("!A", "110001");
        compCodes.put("!M", "110001");
        compCodes.put("-D", "001111");
        compCodes.put("-A", "110011");
        compCodes.put("-M", "110011");
        compCodes.put("D+1", "011111");
        compCodes.put("A+1", "110111");
        compCodes.put("M+1", "110111");
        compCodes.put("D-1", "001110");
        compCodes.put("A-1", "110010");
        compCodes.put("M-1", "110010");
        compCodes.put("D+A", "000010");
        compCodes.put("D+M", "000010");
        compCodes.put("D-A", "010011");
        compCodes.put("D-M", "010011");
        compCodes.put("A-D", "000111");
        compCodes.put("M-D", "000111");
        compCodes.put("D&A", "000000");
        compCodes.put("D&M", "000000");
        compCodes.put("D|A", "010101");
        compCodes.put("D|M", "010101");
    }

    public void initDestCodes() {
        destCodes.put("null", "000");
        destCodes.put("M", "001");
        destCodes.put("D", "010");
        destCodes.put("MD", "011");
        destCodes.put("A", "100");
        destCodes.put("AM", "101");
        destCodes.put("AD", "110");
        destCodes.put("AMD", "111");
    }

    public void initJumpCodes() {
        jumpCodes.put("null", "000");
        jumpCodes.put("JGT", "001");
        jumpCodes.put("JEQ", "010");
        jumpCodes.put("JGE", "011");
        jumpCodes.put("JLT", "100");
        jumpCodes.put("JNE", "101");
        jumpCodes.put("JLE", "110");
        jumpCodes.put("JMP", "111");

    }

    public void parse()
    {
        ListIterator<String> li = this.lines.listIterator();
        while(li.hasNext())
        {
            String line = li.next();

            if(line.startsWith("@"))
            {
                li.set(toBinary(line));
            } else
            {
                li.set(toCInstruction(line));
            }

        }
    }

    public LinkedList<String> getParsedLines() {
        return new LinkedList<>(lines);
    }

    public String toBinary(String aInstruction)
    {
        aInstruction = aInstruction.replace("@", "");
        String binary = null;
        try {
            int value = Integer.parseInt(aInstruction);
            binary = Integer.toBinaryString(value);
            final int size = binary.length();
            for(int i = size; i < 16; i++)
            {
                binary = "0" + binary;
            }
        } catch (NumberFormatException e)
        {
            System.out.println("There was an error parsing " + aInstruction + ".");
        }

        return binary;
    }

    public String toCInstruction(String line)
    {
        if(line.contains("=") && !line.contains(";"))
        {
            //dest=comp
            String[] arr = line.split("=");
            String dest = arr[0];
            String comp = arr[1];
            if(destCodes.get(dest) == null)
            {
                System.out.println("ERROR: Could not parse dest: " + dest);
                System.exit(0);
            }

            if(compCodes.get(comp) == null)
            {
                System.out.println("ERROR: Could not parse comp: " + comp);
                System.exit(0);

            }

            String binaryDest = destCodes.get(dest);
            String binaryComp = compCodes.get(comp);

            String memory = "0";

            if(comp.contains("M"))
            {
                memory = "1";
            }

            String result = "111" + memory + binaryComp + binaryDest + "000";
            return  result;


        } else if(!line.contains("=") && line.contains(";"))
        {
            //comp;jmp
            String[] arr = line.split(";");
            String comp = arr[0];
            String jump = arr[1];
            if(compCodes.get(comp) == null)
            {
                System.out.println("ERROR: Could not parse comp: " + comp);
                System.exit(0);
            }

            if(jumpCodes.get(jump) == null)
            {
                System.out.println("ERROR: Could not parse jump: " + jump);
                System.exit(0);

            }

            String binaryComp = compCodes.get(comp);
            String binaryJump = jumpCodes.get(jump);

            String memory = "0";

            if(comp.contains("M"))
            {
                memory = "1";
            }

            String result = "111" + memory + binaryComp + "000" + binaryJump;
            return  result;
        } else if(line.contains("=") && line.contains(";"))
        {
            //dest=comp;jmp
            String[] arr = line.split("=|;");
            String dest = arr[0];
            String comp = arr[1];
            String jump = arr[2];
            if(destCodes.get(dest) == null)
            {
                System.out.println("ERROR: Could not parse dest: " + dest);
                System.exit(0);
            }

            if(compCodes.get(comp) == null)
            {
                System.out.println("ERROR: Could not parse comp: " + comp);
                System.exit(0);
            }

            if(jumpCodes.get(jump) == null)
            {
                System.out.println("ERROR: Could not parse jump: " + jump);
                System.exit(0);

            }

            String binaryDest = destCodes.get(dest);
            String binaryComp = compCodes.get(comp);
            String binaryJump = jumpCodes.get(jump);


            String memory = "0";

            if(comp.contains("M"))
            {
                memory = "1";
            }

            String result = "111" + memory + binaryComp + binaryDest + binaryJump;
            return  result;
        } else
        {
            System.out.println("ERROR: Could not be parsed Arguments: " + line);
            System.exit(0);
            return null;
        }
    }



}
