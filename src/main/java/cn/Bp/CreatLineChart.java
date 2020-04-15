package cn.Bp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
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
import org.jfree.ui.TextAnchor;

public class CreatLineChart {
    private static String NO_DATA_MSG = "数据加载失败";
    private static Font FONT = new Font("宋体", Font.PLAIN, 12);

    public static Color[] CHART_COLORS = {
            new Color(31,129,188), new Color(92,92,97), new Color(144,237,125), new Color(255,188,117),
            new Color(153,158,255), new Color(255,117,153), new Color(253,236,109), new Color(128,133,232),
            new Color(158,90,102),new Color(255, 204, 102) };//颜色

    public CreatLineChart() {
        setChartTheme();
    }

    /**
     * 中文主题样式 解决乱码
     */
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

    @SuppressWarnings("deprecation")
    public void setLineRender(JFreeChart chart, boolean isShowDataLabels, boolean isShapesVisible) {
        CategoryPlot plot = chart.getCategoryPlot();

        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10, 10, 0, 10), false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setStroke(new BasicStroke(1.5F));

        // render specific data at data point
        if (isShowDataLabels) {
            renderer.setBaseItemLabelsVisible(true);
            renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING,
                    NumberFormat.getInstance()));
            renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));// weizhi
        }
        // render data point
        renderer.setBaseShapesVisible(isShapesVisible);// 数据点绘制形状
        // render X/Y axis
        setXAixs(plot);
        setYAixs(plot);
    }

    /**
     * 设置类别图表(CategoryPlot) X坐标轴线条颜色和样式
     */
    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);// X坐标轴颜色
        plot.getDomainAxis().setTickMarkPaint(lineColor);// X坐标轴标记|竖线颜色
    }

    /**
     * 设置类别图表(CategoryPlot) Y坐标轴线条颜色和样式 同时防止数据无法显示
     */
    public static void setYAixs(CategoryPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);// Y坐标轴颜色
        axis.setTickMarkPaint(lineColor);// Y坐标轴标记|竖线颜色
        // 隐藏Y刻度
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        // Y轴网格线条
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1));

        plot.getRangeAxis().setUpperMargin(0.1);// 设置顶部Y坐标轴间距,防止数据无法显示
        plot.getRangeAxis().setLowerMargin(0.1);// 设置底部Y坐标轴间距

    }

    public DefaultCategoryDataset createDataset(List<String> categories,List<Data> datas) throws Exception{
        assert datas.size() > 0;
        assert datas.get(0).getData().size() == categories.size();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Data cur : datas) {
            String name = cur.getName();
            ArrayList<Double> data = cur.getData();
            for (int i = 0; i < data.size(); i++) {
                Double value = data.get(i);
                // setValue(Number value, Comparable rowKey, Comparable columnKey)
                dataset.setValue(value, name, categories.get(i));
            }
        }
        return dataset;
    }

    /**
     * 创建折线图
     * @param title 折线图标题
     * @param xtitle x轴标题
     * @param ytitle y轴标题
     * @param categorie 横坐标类别
     * @param datas 数据集
     */
    public ChartPanel createChart(String title, String xtitle, String ytitle, List<String> categorie, List<Data> datas) throws Exception {
        // 1. generate dataset
        CategoryDataset dataset = createDataset(categorie, datas);
        // 2：generate chart
        JFreeChart chart = ChartFactory.createLineChart(title, xtitle, ytitle, dataset, PlotOrientation.VERTICAL, true, true, true);
        // 3. configure antiAlias
        chart.setTextAntiAlias(false);
        // 4. configure line render
        setLineRender(chart, false,true);//
        // 5. generate ChartPanel
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
        // 设置标注无边框
        //chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
    }
    /**
     * save as PNG,JPEG
     * @param chartPanel  折线图对象
     * @param outputPath 文件保存路径, 包含文件名
     * @param weight  宽
     * @param height 高
     */
    public static void saveAsFile(ChartPanel chartPanel, String outputPath, int weight, int height)throws Exception {
        JFreeChart chart = chartPanel.getChart();
        FileOutputStream out = null;
        File outFile = new File(outputPath);

        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }else{
            System.out.println("========================");
        }
        out = new FileOutputStream(outputPath);
        // save as PNG
        ChartUtilities.writeChartAsPNG(out, chart, weight, height);
        // save as JPEG
        // ChartUtilities.writeChartAsJPEG(out, chart, weight, height);
        out.flush();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // do nothing
                e.printStackTrace();
            }
        }
    }

    public void draw() throws Exception{
        // train and export data
        //PredictAll driver = new PredictAll();
        Predict driver = new Predict();
        driver.init();
        driver.train();
        driver.show();
        ArrayList<ArrayList<Double>> pair = driver.export();

        // generate ytitle
        List<Data> datas = new Vector<Data>();
        datas.add(new Data("old", pair.get(1)));
        datas.add(new Data("new", pair.get(0)));

        // generate xtitle
        List<String> categorie = new Vector<String>();
        for(int i = 0; i < 30; ++i){
            categorie.add(String.valueOf(i));
        }

        // show picture
        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 420);
        frame.setLocationRelativeTo(null);
        ChartPanel chartPanel = createChart("statistic.png", "xtitle", "ytitle", categorie, datas);
        frame.getContentPane().add(chartPanel);
        frame.setVisible(true);

        // save picture
        //saveAsFile(chartPanel,"D:\\学习\\毕设\\Project\\src\\main\\resources\\demo\\result.png",900,500);

    }

    public static void main(String[] args) throws Exception{
        new CreatLineChart().draw();

        // run swing
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 创建图形
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/
    }
}
