import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.RandomGenerator;

public class TestcaseGenerator {
    private String TestcaseDir;
    public void Generate(String outputFolder,String formatFile,int strength) throws IOException {
        System.out.println("Generating Testcases...");
        setTestcaseDir(outputFolder);
        FileReader fileReader=new FileReader(formatFile);
        BufferedReader bf=new BufferedReader(fileReader);
        String formatString=bf.readLine();
        ArrayList<CaseDescriptor> descriptors=Analyse(formatString);
        GenerateTestcase(Data.TESTCASE_NAME,strength,descriptors,Data.TESTCASE_FORMAT);
        System.out.println("Testcase Generation Finished");
    }
    public void setTestcaseDir(String testcaseDir) {
        TestcaseDir = testcaseDir;
    }
    public void WriteTestcase(String filename,String[] content,String fileFormat) throws IOException {
        File testcase=new File(TestcaseDir+filename+fileFormat);
        if(!testcase.exists()){
            testcase.createNewFile();
        }
        FileWriter writer=new FileWriter(testcase);
        for(String line : content){
            writer.write(line+" ");
        }
        writer.close();
    }
    public ArrayList<CaseDescriptor> Analyse(String format){
        ArrayList<CaseDescriptor> result=new ArrayList<>();
        String[] types=format.split(" ");
        for(String type : types){
            System.out.println(type);
            String[] cut=type.split("\\(");
            switch (cut[0]){
                case "int":{
                    String[] range=cut[1].split(",");
                    range[1]=range[1].substring(0,range[1].length()-1);
                    result.add(new CaseInt(Integer.parseInt(range[0]),Integer.parseInt(range[1])));
                    break;
                }
                case "string":{
                    String[] range=cut[1].split(",");
                    range[1]=range[1].substring(0,range[1].length()-1);
                    result.add(new CaseString(Integer.parseInt(range[0]),Integer.parseInt(range[1])));
                    break;
                }
                case "char":{
                    result.add(new CaseChar());
                    break;
                }
            }
        }
        return result;
    }
    public void GenerateTestcase(String filename, int testcaseSize,ArrayList<CaseDescriptor> formats,String fileFormat) throws IOException {
        int id=0;
        while (id<testcaseSize){
            String[] contents=new String[formats.size()];
            for(int i=0;i<formats.size();++i){
                contents[i] = formats.get(i).GenerateRandomCase();
            }
            WriteTestcase(filename+id,contents,fileFormat);
            ++id;
        }
    }
}
enum CaseType{STRING,INT,CHAR}
abstract class CaseDescriptor{
    public static String CharSet="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    CaseType Type;
    public abstract String GenerateRandomCase();
}
class CaseString extends CaseDescriptor{
    int Min;
    int Max;
    public CaseString(int minLength,int maxLength){
        Min=minLength;
        Max=maxLength;
        Type=CaseType.STRING;
    }
    @Override
    public String GenerateRandomCase() {
        String result="";
        Random random=new Random();
        int length= random.nextInt(Min,Max+1);
        while (result.length()<length){
            result+=CharSet.charAt(random.nextInt(CharSet.length()));
        }
        return result;
    }
}
class CaseInt extends CaseDescriptor{
    int Min;
    int Max;
    public CaseInt(int min,int max){
        Min=min;
        Max=max;
        Type=CaseType.INT;
    }
    @Override
    public String GenerateRandomCase() {
        Random random=new Random();
        int number= random.nextInt(Min,Max+1);
        return ""+number;
    }
}
class CaseChar extends CaseDescriptor{
    public CaseChar(){
        Type=CaseType.CHAR;
    }
    public String GenerateRandomCase(){
        Random random=new Random();
        return ""+CharSet.charAt(random.nextInt(CharSet.length()));
    }
}