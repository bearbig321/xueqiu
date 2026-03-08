package com.xueqiu.etf.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class CsvReportWriter {
    public void writeBacktestSummary(String filePath, Map<String, Object> metrics) {
        try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(filePath), CSVFormat.DEFAULT.withHeader("metric", "value"));
            for (Map.Entry<String, Object> entry : metrics.entrySet()) {
                printer.printRecord(entry.getKey(), entry.getValue());
            }
            printer.flush();
            printer.close();
        } catch (IOException e) {
            throw new IllegalStateException("写出CSV失败: " + e.getMessage(), e);
        }
    }
}
