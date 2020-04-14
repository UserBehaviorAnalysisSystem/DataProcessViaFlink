package cn.csv;

import java.io.*;

public class CsvOp {
    private final String defaultRead = "src/main/resources/UserBehavior.csv";
    private final String defaultWrite = "src/main/resources/out/out1.csv";

    public String read(int n, String... src) throws Exception{
        assert src.length <= 1;
        String filename;
        if(src.length == 0){
            filename = defaultRead;
        }else{
            filename = src[0];
        }
        //先FileReader把文件读出来再bufferReader按行读  reader.readLine()
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        StringBuilder ret = new StringBuilder();
        int index = 0;
        while ((line = reader.readLine()) != null && index <= n - 1) {
            System.out.println(line);
            if(index != n - 1)
                ret.append(line).append("\n");
            else
                ret.append(line);
            index++;
            //String item[] = line.split(",");//一行数组
        }
        reader.close();
        return ret.toString();
    }

    public void write(String in, String... out) throws Exception {
        String dst;
        if(out.length == 0){
            dst = defaultWrite;
        }else{
            dst = out[0];
        }
        File fd = new File(dst);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd));
        String item[] = in.split("\n");//一行数组
        int len = item.length;
        for(int i = 0; i < len; ++i){
            writer.write(item[i]);
            if(i != len -1)
                writer.newLine();
        }
        writer.close();
    }

    public void appendWrite(String s, String out) throws Exception{
        File fd = new File(out);
        BufferedWriter writer = new BufferedWriter(new FileWriter(fd, true));
        String line[] = s.split("\n");
        int len = line.length;
        for(int i = 0; i <= len - 1; ++i){
            writer.write(line[i]);
            writer.newLine();
        }
        writer.close();
    }

    public void copyFrom(int n) throws Exception {
        String s = read(n);
        write(s);
    }

    public static void main(String[] args) throws Exception{
        CsvOp c = new CsvOp();
        c.copyFrom(480000);
    }
}
