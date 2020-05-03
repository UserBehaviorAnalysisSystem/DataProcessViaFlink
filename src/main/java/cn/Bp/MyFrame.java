package cn.Bp;

import cn.zzt.Main;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MyFrame extends JFrame implements Runnable{
    private static Font FONT = new Font("宋体", Font.PLAIN, 12);
    public static Color[] CHART_COLORS = {
            new Color(31,129,188), new Color(92,92,97), new Color(144,237,125), new Color(255,188,117),
            new Color(153,158,255), new Color(255,117,153), new Color(253,236,109), new Color(128,133,232),
            new Color(158,90,102),new Color(255, 204, 102) };//颜色

    private final int MAX = 30;
    private int dataCount = 0;
    private ArrayList<Double> predict = new ArrayList<>();
    private ArrayList<Double> expect = new ArrayList<>();
    private List<String> categories = new Vector<>();

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public MyFrame() throws Exception{
        setChartTheme();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 420);
        this.setLocationRelativeTo(null);


        // 1.generate chart
        JFreeChart chart = ChartFactory.createLineChart("predict.png", "xtitle", "ytitle", dataset, PlotOrientation.VERTICAL, true, true, true);
        //chart.fireChartChanged();

        // 2.configure antiAlias
        chart.setTextAntiAlias(false);

        // 3.configure line render
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setNoDataMessage("数据加载失败");
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setStroke(new BasicStroke(1.5F));
        // render data point
        renderer.setBaseShapesVisible(true);// 数据点绘制形状
        // set X axis
        Color lineColor1 = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor1);// X坐标轴颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor1);// X坐标轴标记|竖线颜色
        // set Y axis
        Color lineColor2 = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor2);// Y坐标轴颜色
        axis.setTickMarkPaint(lineColor2);// Y坐标轴标记|竖线颜色
        // 隐藏Y刻度
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));
        plot.getRangeAxis().setUpperMargin(0.1);// 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setLowerMargin(0.1);// 设置底部Y坐标轴间距

        // 4.generate ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        this.getContentPane().add(chartPanel);

        // 5.show
        this.setVisible(true);
    }
    public static void setChartTheme() {
        // 设置中文主题样式 解决乱码
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        chartTheme.setExtraLargeFont(FONT);
        // 设置图例的字体
        chartTheme.setRegularFont(FONT);
        // 设置轴向的字体
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));

        chartTheme.setLegendBackgroundPaint(Color.WHITE);// 设置标注
        chartTheme.setLegendItemPaint(Color.BLACK);//
        chartTheme.setChartBackgroundPaint(Color.WHITE);
        // 绘制颜色绘制颜色.轮廓供应商
        // paintSequence,outlinePaintSequence,strokeSequence,outlineStrokeSequence,shapeSequence

        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[] { Color.WHITE };
        // 绘制器颜色源
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);

        chartTheme.setPlotBackgroundPaint(Color.WHITE);// 绘制区域
        chartTheme.setPlotOutlinePaint(Color.WHITE);// 绘制区域外边框
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));// 链接标签颜色
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);

        chartTheme.setAxisOffset(new RectangleInsets(5, 12, 5, 12));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));// X坐标轴垂直网格颜色
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));// Y坐标轴水平网格颜色

        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);// 不确定含义
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));// 坐标轴标题文字颜色
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));// 刻度数字
        chartTheme.setBarPainter(new StandardBarPainter());// 设置柱状图渲染
        chartTheme.setXYBarPainter(new StandardXYBarPainter());// XYBar 渲染

        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);// 温度计

        ChartFactory.setChartTheme(chartTheme);
    }

    @Override
    public void run(){
        try {
            while (true) {
                //Thread.sleep(1000);
                // wait for new message
                Data data = Main.queue.take();

                Double latestPredict = data.getPredict(), latestExpect = data.getExpect();
                String s;
                if(dataCount == 0){
                    s = "0";
                }else{
                    s = String.valueOf(Integer.valueOf(categories.get(dataCount - 1)) + 1);
                }

                if(dataCount <= MAX){
                    dataCount++;
                }else{
                    predict.remove(0);
                    expect.remove(0);
                    categories.remove(0);
                }

                predict.add(latestPredict);
                expect.add(latestExpect);
                categories.add(s);
                //System.out.println("predict:" + predict.get(dataCount - 1) + ",expect:" + expect.get(dataCount - 1) + ",categories:" + categories.get(dataCount - 1));

                dataset.clear();
                for(int i = 0; i < dataCount; ++i){
                    dataset.setValue(predict.get(i), "predict", categories.get(i));
                    dataset.setValue(expect.get(i), "expect", categories.get(i));
                }

                Container container = this.getContentPane();
                container.invalidate();
                ChartPanel chartPanel = (ChartPanel)container.getComponent(0);
                JFreeChart jFreeChart = chartPanel.getChart();
                jFreeChart.getCategoryPlot().setDataset(dataset);
                jFreeChart.fireChartChanged();
                container.validate();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 创建图形
                try {
                    //showDemo();
                    MyFrame myFrame = new MyFrame();
                    //c.draw();
                    (new Thread(myFrame)).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
