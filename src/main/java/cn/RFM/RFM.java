package cn.RFM;

import java.sql.*;
import java.util.ArrayList;

public class RFM {
    public static class people{
        public int userId;
        public String recency;
        public int frequency;
        public int monetary;
    }
    void connect() throws Exception{
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://localhost:3306/flink?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
        String USER = "root", PASS = "zztdcyy";

        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
        Statement stmt = conn.createStatement();
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
        stmt.close();
        conn.close();
    }
    public static void main(String[] args) throws Exception{
        RFM rfm = new RFM();
        rfm.connect();
    }
}
