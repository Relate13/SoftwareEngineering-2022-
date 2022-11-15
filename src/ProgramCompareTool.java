import java.io.*;
import java.util.ArrayList;

public class ProgramCompareTool {
    private String Root;
    private int TestCaseStrength =5;
    public void SetTestCaseStrength(int strength){
        TestCaseStrength=strength;
    }
    public void SetRoot(String dir){
        Root =dir;
    }
    public void StartCompareRoutine() throws IOException, InterruptedException {
        System.out.println("Starting Compare Routine on Directory "+Root+"...");
        System.out.println("Current Testcase Strength: "+TestCaseStrength);
        File RootFile=new File(Root);
        for(File f : RootFile.listFiles()){
            if (f.isDirectory()) {
                System.out.println("Handling :"+f.getAbsolutePath()+"/");
                CompareTask(f.getAbsolutePath()+"/");
            }
        }
    }
    public void CompareTask(String subDir) throws IOException, InterruptedException {
        String testcasePATH=subDir+Data.TESTCASE_PATH;
        String programPATH=subDir+Data.BUILD_PATH;
        String resultPATH=subDir+Data.RESULT_PATH;
        String testcaseFormatPATH=subDir+Data.FORMAT_FILE_NAME;
        //create testcase folder
        File testcaseFolder=new File(testcasePATH);
        testcaseFolder.mkdir();
        //create program folder
        File programFolder=new File(programPATH);
        programFolder.mkdir();
        //create result folder
        File resultFolder=new File(resultPATH);
        resultFolder.mkdir();
        // generate testcase
        TestcaseGenerator t=new TestcaseGenerator();
        t.Generate(testcasePATH,testcaseFormatPATH, TestCaseStrength);
        Batch srcBatch = GenerateBatchFromFolder(subDir);
        Batch programBatch = CompileSrc(srcBatch,programPATH);
        for (File program: programBatch.Files) {
            ProgramInjector pj=new ProgramInjector(program);
            pj.InjectTestcase(testcasePATH,resultPATH);
        }
        programBatch = Compare(programBatch,resultPATH);
        String OutDir=new File(Root).getParentFile().getAbsolutePath()+'/'+Data.OUTPUT_DIR;

        File OutDirFile=new File(OutDir);
        if(!OutDirFile.exists())
            OutDirFile.mkdir();
        //System.out.println("Out folder:"+OutDirFile.getAbsolutePath());
        String[] split=subDir.split("/");
        String GroupID=split[split.length-1];
        GenerateResult(programBatch,srcBatch,OutDirFile.getAbsolutePath()+"/",GroupID);
        for(File f:testcaseFolder.listFiles()){
            f.delete();
        }
        testcaseFolder.delete();
        for(File f:programFolder.listFiles()){
            f.delete();
        }
        programFolder.delete();
        for(File f:resultFolder.listFiles()){
            f.delete();
        }
        resultFolder.delete();
    }
    public Batch GenerateBatchFromFolder(String subDir){
        File mainDir=new File(subDir);
        if(mainDir.exists()){
            ArrayList<File> srcList=new ArrayList<>();
            for (File f: mainDir.listFiles()) {
                //System.out.println(f.getName());
                String[] split=f.getName().split("\\.");
                switch (split[split.length-1]){
                    case "cpp","c":srcList.add(f);
                    default:break;
                }
            }
            Batch srcBatch=new Batch(srcList.size());
            for(int i=0;i<srcList.size();++i){
                System.out.println(srcList.get(i).getName());
                srcBatch.Files[i]=srcList.get(i);
            }
            return srcBatch;
        }
        else {
            System.out.println("Folder do not exist");
            return null;
        }
    }
    public Batch CompileSrc(Batch srcBatch,String OutputFolder) throws IOException, InterruptedException {
        System.out.println("Starting Compiling...");
        Batch programBatch=new Batch(srcBatch.size);
        for(int i=0;i<srcBatch.Files.length;++i){
            File src=srcBatch.Files[i];
            String filename=src.getName().split("\\.")[0];
            String command = "g++ " + src.getAbsolutePath()+" -o "+OutputFolder+filename;
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            programBatch.Files[i]=new File(OutputFolder+filename);
        }
        System.out.println("Compile Process Finished");
        return programBatch;
    }
    public Batch Compare(Batch programBatch,String ResultFolder){
        String[] HashList=new String[programBatch.size];
        System.out.println("Generating Hash...");
        for(int i=0;i<programBatch.Files.length;++i){
            File A=programBatch.Files[i];
            String ResultNameA=A.getName().split("\\.")[0]+Data.OUTPUT_FORMAT;
            File ResultA=new File(ResultFolder+ResultNameA);
            HashList[i]=SHA1.generate(ResultA);
            System.out.println(HashList[i]);
        }
        System.out.println("Generation Succeed");
        for(int i=1;i<programBatch.Files.length;++i){
            for(int j=0;j<i;++j){
                if(programBatch.EqData.find(i)!=programBatch.EqData.find(j)) {
                    if(HashList[i].equals(HashList[j])){
                        programBatch.EqData.union(i,j);
                    }
                }
            }
        }
        return programBatch;
    }
    public void GenerateResult(Batch programBatch,Batch srcBatch,String outputFolder,String GroupID) throws IOException {
        ArrayList<String> equalData=new ArrayList<>();
        ArrayList<String> inequalData=new ArrayList<>();
        for(int i=1;i<programBatch.size;++i){
            for (int j=0;j<i;++j){
                String PairString=srcBatch.Files[i].getName()+","+srcBatch.Files[j].getName();
                if(programBatch.EqData.find(i)==programBatch.EqData.find(j)){
                    equalData.add(PairString);
                }
                else{
                    inequalData.add(PairString);
                }
            }
        }
        File EqualFile =new File(outputFolder+GroupID+"-equal-pairs.csv");
        if(!EqualFile.exists())
            EqualFile.createNewFile();
        FileWriter writer = new FileWriter(EqualFile);
        writer.write("file1,file2\n");
        for (String e:equalData) {
            //System.out.println(e);
            writer.write(e+"\n");
        }
        writer.close();
        System.out.println("Successfully written to "+EqualFile.getAbsolutePath());
        //File InEqualFile=new File(path+"\\"+GroupID+"-inequal-pairs.csv");
        File InEqualFile =new File(outputFolder+GroupID+"-inequal-pairs.csv");
        if(!InEqualFile.exists())
            InEqualFile.createNewFile();
        writer = new FileWriter(InEqualFile);
        writer.write("file1,file2\n");
        for (String e:inequalData) {
            //System.out.println(e);
            writer.write(e+"\n");
        }
        writer.close();
        System.out.println("Successfully written to "+InEqualFile.getAbsolutePath());
    }
}
class ProgramInjector{
    File program;
    public ProgramInjector(File program){
        this.program=program;
    }
    public void SetProgram(File executable){
        program=executable;
    }
    public File InjectTestcase(String testcaseFolder,String outputFolder) throws IOException, InterruptedException {
        String filename=program.getName();
        System.out.println("Starting Testcase Injection for "+filename+"...");
        File tf=new File(testcaseFolder);
        File[] testcases=tf.listFiles();
        File script=new File(outputFolder+"script.sh");
        if(script.exists())
            script.delete();
        script.createNewFile();
        FileWriter writer=new FileWriter(script);
        for (File t: testcases) {
            String command = program.getAbsolutePath() + " < " + t.getAbsolutePath()+" >> "+outputFolder+filename+Data.OUTPUT_FORMAT;
            writer.write(command+'\n');
            System.out.println(command);
        }
        writer.close();
        Process process = Runtime.getRuntime().exec("chmod +x "+outputFolder+"script.sh");
        process.waitFor();
        process = Runtime.getRuntime().exec(outputFolder+"script.sh");
        process.waitFor();
        script.delete();
        System.out.println("Injection for "+filename+" is Finished");
        return null;
    }
}
