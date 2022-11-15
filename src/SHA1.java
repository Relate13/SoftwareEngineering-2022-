import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class SHA1 {

    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E','F' };

    /**
     * @Description: 计算SHA1码
     * @Param file
     * @Return java.lang.String
     * @Author Menglong.Cao
     * @Date 2022/3/1 16:43
     */
    public static String generate(File file){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,0,file.length());
            messageDigest.update(byteBuffer);
            return bufferToHex(messageDigest.digest());
        }catch (Exception e){
            System.out.println("generate-hash-fail:errorMessage={},{}"+e.getMessage()+e);
        }
        return null;

    }

    private static String bufferToHex(byte bytes[]){
        return bufferToHex(bytes,0,bytes.length);
    }

    private static String bufferToHex(byte bytes[],int m,int n){
        StringBuffer stringBuffer = new StringBuffer(2*n);
        int k = m + n;
        for(int l = m; l < k; l++){
            appendHexPair(bytes[l],stringBuffer);
        }
        return stringBuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringBuffer){
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringBuffer.append(c0);
        stringBuffer.append(c1);
    }

    //测试
    public static void main(String[] args) throws Exception{
        System.out.println("begin...");
        long begin = System.currentTimeMillis();
        File file2 = new File("D:\\计算hash.txt");
        String sha1 = SHA1.generate(file2);
        System.out.println("文件hash:"+sha1);// 82C189E0D231901FFC0597401F75CB54F44331A8
        System.out.println("耗时："+(System.currentTimeMillis()-begin)+"ms");
        System.out.println("end...");
    }

}
