package cn.Bp;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class RealTimeChart extends ChartPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    private static TimeSeries timeSeries;

    public RealTimeChart(JFreeChart chart) {
        super(chart);
    }

    public RealTimeChart(String chartContent, String title, String yaxisName) {
        super(createChart(chartContent, title, yaxisName));
    }

    private static JFreeChart createChart(String chartContent, String title, String yaxisName) {
        // 创建时序图对象
        timeSeries = new TimeSeries(chartContent, Millisecond.class);
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "时间(秒)", yaxisName, timeseriescollection,true, true, false);
        XYPlot xyplot = jfreechart.getXYPlot();
        // 纵坐标设定
        ValueAxis valueaxis = xyplot.getDomainAxis();
        // 自动设置数据轴数据范围
        valueaxis.setAutoRange(true);
        // 数据轴固定数据范围 30s
        valueaxis.setFixedAutoRange(30000D);

        valueaxis = xyplot.getRangeAxis();
        // valueaxis.setRange(0.0D,200D);
        return jfreechart;
    }

    private long randomNum() {
        System.out.println((Math.random() * 20 + 80));
        return (long) (Math.random() * 20 + 80);
    }

    @Override
    public void run() {
        while (true) {
            try {
                timeSeries.add(new Millisecond(), randomNum());
                Thread.sleep(300);
                System.out.println("====================add======================");
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Chart");
        RealTimeChart rtcp = new RealTimeChart("Random Data", "随机数", "数值");
        frame.getContentPane().add(rtcp, new BorderLayout().CENTER);
        frame.pack();
        frame.setVisible(true);
        (new Thread(rtcp)).start();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                System.exit(0);
            }
        });
    }
}
