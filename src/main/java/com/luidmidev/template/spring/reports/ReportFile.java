package com.luidmidev.template.spring.reports;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ReportFile {
    private String idFilePDF;
    private String idFileExcel;
    private Map<String, Double> data = new HashMap<>();
}
