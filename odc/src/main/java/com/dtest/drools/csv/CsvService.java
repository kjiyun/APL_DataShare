package com.dtest.drools.csv;

import com.dtest.drools.global.apipayload.code.status.ErrorStatus;
import com.dtest.drools.global.apipayload.exception.GeneralException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvService {
    public List<String> getCsvHeaders(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] headers = reader.readNext();
            return headers != null ? Arrays.asList(headers) : List.of();
        } catch (IOException | CsvValidationException e) {
            throw new GeneralException(ErrorStatus.CANNOT_READ_CSV);
        }
    }
}
