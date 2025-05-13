package apl.filtering;

import apl.filtering.frame.Attribute;
import apl.filtering.frame.DataRecord;
import apl.filtering.global.util.CSVHandler;
import apl.filtering.global.util.SavePath;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

@Slf4j
public class FilteringApplication {

    public static void main(String[] args) throws CsvValidationException, IOException {
        CSVHandler csvHandler = new CSVHandler();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(new File(SavePath.JSON.getPath()));

            List<String> odcAttributes = objectMapper.readValue(rootNode.get("odcAttributes").toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            List<String> udcAttributes = objectMapper.readValue(rootNode.get("udcAttributes").toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

            Map<Attribute, String> mappingInfo = new LinkedHashMap<>();

            for (int i = 0; i < odcAttributes.size(); i++) {
                mappingInfo.put(Attribute.from(udcAttributes.get(i)), odcAttributes.get(i));
            }

            csvHandler.write(udcAttributes, "label");

            List<DataRecord> dataRecords = csvHandler.getDataRecords(mappingInfo);

            KieContainer kc = KieServices.get().getKieClasspathContainer();
            StatelessKieSession ks = kc.newStatelessKieSession("DataKS");
            ks.setGlobal("csvHandler", csvHandler);
            ks.execute(dataRecords);
            kc.dispose();

            csvHandler.save();

        } catch (IOException | CsvException e) {
            log.info(e.getMessage());
        }
    }

}
