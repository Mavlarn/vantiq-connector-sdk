package io.vantiq.extjsdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static io.vantiq.extjsdk.ConnectorConstants.*;


public class ConnectorConfig {

    public static final String LOCAL_CONFIG_FILE_NAME = "config.json";
    static final Logger log = LoggerFactory.getLogger(ConnectorConfig.class);

    private String vantiqUrl;
    private String token;
    private String sourceName;

    public ConnectorConfig() {

        String configFileName = System.getProperty("user.dir") + File.separator + LOCAL_CONFIG_FILE_NAME;
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            configFile = new File("/etc/config/config.json");
        }
        InputStream cfr = null;
        Map<String, Object> props;
        try {
            if (configFile.exists()) {
                cfr = new FileInputStream(configFileName);
                ObjectMapper mapper = new ObjectMapper();
                props = mapper.readValue(configFile, Map.class);

                vantiqUrl = (String)props.get(VANTIQ_URL);
                token = (String)props.get(VANTIQ_TOKEN);
                sourceName = (String)props.get(VANTIQ_SOURCE_NAME);
            } else {
                log.error("Location specified for configuration directory ({}) does not exist.", configFile.getAbsolutePath());
                throw new RuntimeException("No configuration file");
            }

            if (vantiqUrl == null || token == null || sourceName == null) {
                log.error("Invalid parameters.");
                throw new RuntimeException("Invalid parameters in configuration file");
            }

        } catch (IOException e) {
            log.error("Config file ({}) was not readable: {}", configFileName, e.getMessage());
            throw new RuntimeException("Invalid configuration file");
        } finally {
            if (cfr != null) {
                try {
                    cfr.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        log.debug("Connector Config finish for name: {}", sourceName);
    }

    public String getVantiqUrl() {
        return vantiqUrl;
    }

    public void setVantiqUrl(String vantiqUrl) {
        this.vantiqUrl = vantiqUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
