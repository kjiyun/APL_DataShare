package apl.filtering.global.util;

import apl.filtering.frame.Attribute;
import apl.filtering.frame.DataRecord;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CSVHandler {

//    private final List<String[]> data = new ArrayList<>();
    private final CSVReader csvReader;
    private final CSVWriter csvWriter;
    private final List<String> header;
    private final List<String[]> data;

    public CSVHandler() throws IOException, CsvValidationException {
        this.csvReader = new CSVReader(new FileReader(SavePath.ORIGINAL.getPath()));
        this.csvWriter = new CSVWriter(new FileWriter(SavePath.FILTERED.getPath()));
        this.header = Arrays.asList(csvReader.readNext());
        this.data = new ArrayList<>();
    }

    public List<DataRecord> getDataRecords(Map<Attribute, String> mappingInfo) throws IOException, CsvException {
        List<String[]> data = csvReader.readAll();

        Map<Attribute, Integer> indexOfAttribute = new LinkedHashMap<>();
        mappingInfo.forEach((key, value) -> {
            indexOfAttribute.put(key, header.indexOf(value));
        });

        return data.stream().map(d -> {
            Map<Attribute, String> info = new LinkedHashMap<>();
            indexOfAttribute.forEach((key, value) -> info.put(key, d[value]));
            return DataRecord.from(info);
        }).toList();
    }

    public void write(List<String> row, String label) {
        row.add(label);
        data.add(row.toArray(String[]::new));
    }

    public void save() throws IOException {
        data.forEach(csvWriter::writeNext);
        csvWriter.close();
    }

    public void writeResult(String outputPath, List<DataRecord> dataRecords) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
            if (!dataRecords.isEmpty()) {
                List<String> headers = dataRecords.get(0).getAttributes();
                writer.writeNext(headers.toArray(new String[0]));
            }

            for (DataRecord dataRecord : dataRecords) {
                List<String> values = dataRecord.getAttributes();
                writer.writeNext(values.toArray(new String[0]));
            }
        }
    }

}
