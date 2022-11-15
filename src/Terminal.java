import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Terminal {
    ProgramCompareTool t = new ProgramCompareTool();
    public void StartListening() throws IOException, InterruptedException {
        Scanner scanner=new Scanner(System.in);
        String input;
        boolean Run=true;
        while (Run){
            input=scanner.nextLine();
            Run = ParseCommand(input);
        }
    }
    public boolean ParseCommand(String input) throws IOException, InterruptedException {
        //System.out.println(input);
        String[] tokens = input.split(" ");
        if(tokens.length==0)
            return true;
        switch (tokens[0]) {
            case "strength" -> {
                if (tokens.length <= 1)
                    System.out.println("Invalid Syntax");
                else {
                    int Strength;
                    try {
                        Strength = Integer.parseInt(tokens[1]);
                    } catch (Exception e) {
                        System.out.println("Invalid Syntax");
                        return true;
                    }
                    t.SetTestCaseStrength(Strength);
                    System.out.println("Strength set to " + Strength);
                }
                return true;
            }
            case "dir" -> {
                if (tokens.length <= 1)
                    System.out.println("Invalid Syntax");
                else {
                    String RootDIR;
                    File aim = new File(tokens[1]);
                    if (aim.exists() && aim.isDirectory()) {
                        RootDIR = aim.getAbsolutePath() + "/";
                        t.SetRoot(RootDIR);
                        t.StartCompareRoutine();
                    } else {
                        System.out.println("Invalid File Path");
                    }
                }
                return true;
            }
            case "exit" -> {
                System.out.println("Have a nice day...");
                return false;
            }
            default -> {
                System.out.println("Invalid Command");
                return true;
            }
        }
    }
}
