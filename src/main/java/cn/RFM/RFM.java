package cn.RFM;

import cn.zzt.Main;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class RFM {
    private static int topN = 15;
    private static Queue<UserInfo> cache = new PriorityQueue<>();
    private static Set<Long> userIds = new HashSet<>();
    //private static ArrayList<UserInfo> cache = new ArrayList<>();

    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL = "jdbc:mysql://localhost:3306/flink?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private String USER = "root", PASS = "zztdcyy";

    private Connection connection = null;
    private Statement statement = null;

    public void connect() throws Exception{
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DB_URL,USER,PASS);
        statement = connection.createStatement();
        /*Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM people";
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            int userId = rs.getInt("userId");
            String recency = rs.getString("recency");
            int frequency = rs.getInt("frequency");
            int monetary = rs.getInt("monetary");
            System.out.println(userId + ", " + recency + ", " + frequency + ", " + monetary);
        }
        rs.close();
        stmt.close();*/
    }

    public void update(long Id, long timestamp) throws Exception{
        String id = String.valueOf(Id);
        String sql = "SELECT * FROM people WHERE userId = " + id;
        ResultSet rs = statement.executeQuery(sql);
        boolean isExist = false;
        UserInfo userInfo = null;
        while(rs.next()){
            int userId = rs.getInt("userId");
            long recency = Long.valueOf(rs.getString("recency"));
            int frequency = rs.getInt("frequency");
            int monetary = rs.getInt("monetary");
            System.out.println(userId + ", " + recency + ", " + frequency + ", " + monetary);

            userInfo = new UserInfo((long)userId, recency, (long)frequency, (long)monetary);
            Double timeGap = Double.valueOf(String.valueOf(timestamp - recency));
            Double val = - timeGap / (1000 * 60 * 60 * 24) + frequency + 1;
            userInfo.setVal(val);
            isExist = true;
        }


        if(!isExist){
            System.out.println("new user, insert it");
            // update mysql
            String insert = "insert into people(userId, recency, frequency, monetary) values('"
                    + String.valueOf(Id) + "', '"
                    + String.valueOf(timestamp) + "', '"
                    + String.valueOf(1) + "', '"
                    + String.valueOf(0) + "')";
            //System.out.println("sql: " + insert);
            statement.executeUpdate(insert);
            // does not need to update cache
        }else{
            System.out.println("exist user, update it");
            String update1 = "update people set recency='" + String.valueOf(timestamp) + "' where userId='" + String.valueOf(Id) + "'";
            String update2 = "update people set frequency='" + String.valueOf(userInfo.frequency + 1) + "' where userId='" + String.valueOf(Id) + "'";
            //System.out.println("sql: " + update1);
            //System.out.println("sql: " + update2);
            statement.executeUpdate(update1);
            statement.executeUpdate(update2);
            UserInfo cur = new UserInfo(Id, timestamp, userInfo.frequency + 1, 0);
            cur.setVal(userInfo.getVal());
            if(userIds.contains(Id)){
                // cache exist
                for(UserInfo u: cache){
                    if(u.userId == Id){
                        //u.frequency = cur.frequency;
                        //u.monetary = cur.monetary;
                        //u.setVal(cur.getVal());
                        cache.remove(u);
                        cache.add(cur);
                        break;
                    }
                }
            }else{
                // new user
                if(cache.size() < topN){
                    // has empty pos
                    userIds.add(Id);
                    cache.add(cur);
                }else{
                    // replace the least val one
                    cache.add(cur);
                    userIds.add(Id);
                    UserInfo toDelete = cache.poll();
                    userIds.remove(toDelete.userId);
                }
            }

            // show
            Set<UserInfo> tmp = new HashSet<>();
            int len = cache.size(), index = len - 1;
            while(!cache.isEmpty()){
                UserInfo u = cache.poll();
                tmp.add(u);
                //System.out.println("val: " + u.getVal());
                Main.rowData2[index][1] = u.userId;
                Main.rowData2[index][2] = u.val;
                index--;
            }
            // restore
            for(UserInfo u: tmp){
                cache.add(u);
            }

            Main.containerLock.lock();
            Container container = Main.myFrame.getContentPane();
            container.invalidate();
            JPanel jPanel = (JPanel)container.getComponent(2);
            jPanel.remove(1);
            JTable jTable = new JTable(Main.rowData2, Main.columnNames2);
            jPanel.add(jTable, 1);
            container.validate();
            Main.containerLock.unlock();
        }


    }

    public static void main(String[] args) throws Exception{
        RFM rfm = new RFM();
        rfm.connect();
        rfm.update(123, 1000);
        rfm.update(124, 1000);
        rfm.update(125, 1000);
        rfm.update(126, 1000);

        rfm.update(123, 2000);
        rfm.update(124, 3000);
        rfm.update(125, 4000);
        rfm.update(126, 5000);

        while(!cache.isEmpty()){
            UserInfo userInfo = cache.poll();
            System.out.println(userInfo.getVal());
        }
    }
}
