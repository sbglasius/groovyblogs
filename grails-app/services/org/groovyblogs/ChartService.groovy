package org.groovyblogs
import grails.plugin.cache.Cacheable
import grails.gorm.transactions.Transactional
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.CategoryLabelPositions
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.encoders.KeypointPNGEncoderAdapter
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.title.TextTitle
import org.jfree.data.category.DefaultCategoryDataset

import java.awt.*
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

@Transactional(readOnly = true)
class ChartService {

    @Cacheable('chart')
    byte[] getChart() {
        log.debug("Building chart")
        DefaultCategoryDataset dataset = new DefaultCategoryDataset()

        def fmt = new SimpleDateFormat("EEE")
        def entryCount = [:]

        def lastWeek = new Date() - 6

        // Normalise all post dates to midnight on the day they were posted for charting
        BlogEntry.findAllByDateAddedGreaterThan(lastWeek).each { entry ->

            //def key = fmt.format(entry.dateAdded)
            def entryKey = Calendar.getInstance()
            entryKey.set(1900 + entry.dateAdded.year, entry.dateAdded.month, entry.dateAdded.date, 0, 0, 0)
            entryKey.set(Calendar.MILLISECOND, 0)
            def key = entryKey.getTimeInMillis()
            entryCount[key] = (entryCount[key] ?: 0) + 1.0
        }

        // sort the dates into ascending order
        def sortedEntries = entryCount.keySet().sort() // { e1, e2 ->
        // return e2 <=> e1
        //}
        //println "Sorted\n" + "x" * 50 + "\n" + sortedEntries + "\n" + "x" * 50

        sortedEntries.each { key ->
            log.debug("Set $key to ${entryCount[key]}")

            // dataset.addValue(entryCount[key], "Entries", key)
            dataset.addValue(entryCount[key], "Entries", fmt.format(key))
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "entries",                // chart title
                null,                     // domain axis label
                null,                     // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                false,                    // include legend
                false,                    // tooltips
                false                     // urls
        )

        chart.setTitle(new TextTitle("Entries Last 7 Days", new Font("SansSerif", Font.BOLD, 12)))

        CategoryPlot plot = chart.getPlot() as CategoryPlot
        plot.setForegroundAlpha(0.5f)
        plot.setBackgroundPaint(Color.lightGray)
        plot.setRangeGridlinePaint(Color.white)

        CategoryAxis domainAxis = plot.getDomainAxis()
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45)
        // domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 20))

        // customise the range axis...
        NumberAxis rangeAxis = plot.getRangeAxis()
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits())

        // From Aaron's tranparency howto
        // http://cephas.net/blog/2006/06/09/transparent-png-charts-with-jfreechart/
        chart.setBackgroundPaint(new Color(255, 255, 255, 0))
        KeypointPNGEncoderAdapter encoder = new KeypointPNGEncoderAdapter()
        encoder.setEncodingAlpha(true)

        encoder.encode(chart.createBufferedImage(170, 150, BufferedImage.BITMASK, null))
    }

}
