package cn.WatermarkFunction;

import cn.Kafka.JsonHelper;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;

public class assignTimestampsAndWatermarks implements AssignerWithPunctuatedWatermarks<String> {
    @Override
    public long extractTimestamp(String element, long previousElementTimestamp) {
        return JsonHelper.getTimeLongFromRawMessage(element);
    }

    @Nullable
    @Override
    public Watermark checkAndGetNextWatermark(String lastElement, long extractedTimestamp) {
        if (lastElement != null) {
            return new Watermark(JsonHelper.getTimeLongFromRawMessage(lastElement));
        }
        return null;
    }
}
