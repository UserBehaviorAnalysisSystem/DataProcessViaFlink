package cn.WatermarkFunction;

import cn.Kafka.JsonHelper;
import cn.zzt.UserBehavior;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class assignUserBehaviorTimestampAndWatermarks implements AssignerWithPunctuatedWatermarks<String> {
    @Override
    public long extractTimestamp(String element, long previousElementTimestamp) {
        // must be millsecond!!!
        return UserBehavior.parseTimeStamp(element);
    }

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(String lastElement, long extractedTimestamp) {
        if (lastElement != null) {
            // must be millsecond!!!
            return new Watermark(UserBehavior.parseTimeStamp(lastElement));
        }
        return null;
    }

}
