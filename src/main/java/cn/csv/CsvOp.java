package cn.csv;

import cn.zzt.UserBehavior;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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

    public void generateDataset(String src, String dst, int n) throws Exception{
        //String filename = "D:\\学习\\毕设\\Project\\src\\main\\resources\\UserBehaviorLarge.csv";
        //String result = "D:\\学习\\毕设\\Project\\src\\main\\resources\\final\\rawDatas.csv";
        BufferedReader reader = new BufferedReader(new FileReader(src));
        BufferedWriter writer = new BufferedWriter(new FileWriter(dst, true));
        String line = null;
        int index = 0;
        ArrayList<UserBehavior> dataset = new ArrayList<>();
        while((line = reader.readLine()) != null && index < n){
            String[] data = line.split(",");
            UserBehavior userBehavior = new UserBehavior(Long.valueOf(data[0]), Long.valueOf(data[1]), Integer.valueOf(data[2]), data[3], Long.valueOf(data[4]));
            // start from 2017-11-26 09:00:00
            if(userBehavior.getTimestamp() >= 1511657000 && userBehavior.getTimestamp() < 1511658000){
                dataset.add(userBehavior);
            }
            index++;
        }
        Collections.sort(dataset, new Comparator<UserBehavior>() {
            @Override
            public int compare(UserBehavior o1, UserBehavior o2) {
                // increase
                return Long.valueOf(o1.getTimestamp()).compareTo(Long.valueOf(o2.getTimestamp()));
            }
        });
        for(UserBehavior userBehavior: dataset){
            StringBuilder sb = new StringBuilder();
            sb.append(userBehavior.getUserId()).append(",")
                    .append(userBehavior.getItemId()).append(",")
                    .append(userBehavior.getCategoryId()).append(",")
                    .append(userBehavior.getBehavior()).append(",")
                    .append(userBehavior.getTimestamp());
            writer.write(sb.toString());
            writer.newLine();
        }
        reader.close();
        writer.close();
    }

    public static void main(String[] args) throws Exception{
        CsvOp c = new CsvOp();
        //c.generateDataset(1000000);
    }
}
