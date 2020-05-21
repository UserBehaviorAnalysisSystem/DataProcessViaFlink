package cn.RFM;

import java.util.*;

public class UserInfo implements Comparable{
    public long userId;
    public long recency;
    public long frequency;
    public long monetary;

    public Double val;

    public void setVal(Double val) {
        this.val = val;
    }
    public Double getVal(){
        return this.val;
    }

    UserInfo(){
        val = 0.0;
    }

    UserInfo(long id, long r, long f, long m){
        userId = id;
        recency = r;
        frequency = f;
        monetary = m;
    }
    UserInfo(Double v){
        val = v;
    }

    @Override
    public int compareTo(Object o){
        // Ascending order
        UserInfo tmp = (UserInfo)o;
        if(tmp.val == val)
            return 0;
        else if(val < tmp.val)
            return -1;
        else
            return 1;
    }

    public static void main(String[] args) throws Exception{
        Queue<UserInfo> q = new PriorityQueue<>();
        q.add(new UserInfo(1.9823148148148149));
        q.add(new UserInfo(1.9997800925925926));
        q.add(new UserInfo(4.0));

        while(!q.isEmpty()){
            UserInfo userInfo = q.poll();
            System.out.println(userInfo.val);
        }

        /*Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.remove(2);*/
    }
}
