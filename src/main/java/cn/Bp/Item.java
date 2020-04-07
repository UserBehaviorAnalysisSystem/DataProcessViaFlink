package cn.Bp;

import java.util.ArrayList;

/*
 * data format: 14列
 * date; price; numOfBedRoom; numOfbathroom; areaOfHouse; areaOfParking; numOfFloors; grade; area; areaOfUnderroom; yearOfBuilding; yearOfRepair; 纬度； 经度
 * 第二列就是想要predict的东西！！！
 */
public class Item {
    private String date;
    private Double bedroomNum;
    private Double bathroomNum;
    private Double roomSize;
    private Double parkingArea;
    private Double floorNum;
    private Double grade;
    private Double area;
    private Double basementArea;
    private Double constructionYear;
    private Double repairYear;
    private Double latitude;
    private Double longitude;
    public Item(String s) throws Exception{
        String[] v = s.split(",");
        int len = v.length;
        date = v[0];
        bedroomNum = Double.valueOf(v[1]);
        bathroomNum = Double.valueOf(v[2]);
        roomSize = Double.valueOf(v[3]);
        parkingArea = Double.valueOf(v[4]);
        floorNum = Double.valueOf(v[5]);
        grade = Double.valueOf(v[6]);
        area = Double.valueOf(v[7]);
        basementArea = Double.valueOf(v[8]);
        constructionYear = Double.valueOf(v[9]);
        repairYear = Double.valueOf(v[10]);
        latitude = Double.valueOf(v[11]);
        longitude = Double.valueOf(v[12]);
    }

    public ArrayList<Double> toData() throws Exception{
        ArrayList<Double> ret = new ArrayList<>();
        ret.add(grade);
        ret.add(area / 1000);

        // other 10 attribution
        //ret.add(bedroomNum);
        //ret.add(bathroomNum);
        ret.add(roomSize / 1000);
        //ret.add(parkingArea / 1000);
        //ret.add(floorNum);
        //ret.add(basementArea / 100);
        //ret.add(constructionYear);
        //ret.add(repairYear);
        //ret.add(latitude / 10);
        //ret.add(longitude / 100);
        return ret;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("date:").append(date)
                .append("bedroom:").append(bedroomNum)
                .append("bathroomNum:").append(bathroomNum)
                .append("roomSize:").append(roomSize)
                .append("parkingArea:").append(parkingArea)
                .append("floorNum:").append(floorNum)
                .append("grade:").append(grade)
                .append("area:").append(area)
                .append("basementArea:").append(basementArea)
                .append("constructionYear:").append(constructionYear)
                .append("repairYear:").append(repairYear)
                .append("latitude:").append(latitude)
                .append("longitude:").append(longitude);
        return builder.toString();
    }
}
