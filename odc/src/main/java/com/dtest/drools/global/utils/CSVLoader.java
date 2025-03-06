package com.dtest.drools.global.utils;

import com.dtest.drools.patients.PatientData;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader {
    public List<PatientData> loadData(String filepath) throws IOException, CsvException {
        List<PatientData> patientDataList = new ArrayList<>();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true) // 잘못된 인용부호를 무시
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filepath))
                .withCSVParser(parser)
                .build()) {
            String[] headers = reader.readNext();
            if (headers == null) {
                throw new IllegalArgumentException("CSV 파일이 비어 있습니다.");
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                try {
                    PatientData patientData = new PatientData();

                    String heartRateStr = line[findColumnIndex(headers, "triage_heartrate")].trim();
                    patientData.setHeartRate(isNumeric(heartRateStr) ? (int) Double.parseDouble(heartRateStr) : -1);

                    String oxygenSaturationStr = line[findColumnIndex(headers, "triage_o2sat")].trim();
                    patientData.setOxygenSaturation(isNumeric(oxygenSaturationStr) ? (int) Double.parseDouble(oxygenSaturationStr) : -1);

                    patientDataList.add(patientData);
                } catch (NumberFormatException e) {
                    System.err.println("숫자 변환 실패 - 행 건너뜀: " + String.join(",", line));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("인덱스 오류 - 행 건너뜀: " + String.join(",", line));
                }
            }
        }
        return patientDataList;
    }
    private int findColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("헤더에 열 이름이 존재하지 않습니다: " + columnName);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());  // 정수와 소수 모두 확인
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
