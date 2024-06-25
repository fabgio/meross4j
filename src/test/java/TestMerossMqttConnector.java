import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossMqttConnector.class);
    @Test
    void testBuildResponseTopicIsNotNull(){
        MerossMqttConnector.setUserId("3807527");
        String responseTopic = MerossMqttConnector.buildResponseTopic();
        logger.info(responseTopic);
        assertNotNull(responseTopic);
    }
    @Test
    void testBuildToggleXMessage(){
        MerossMqttConnector.setUserId("3807527");
        MerossMqttConnector.setDestinationDeviceUUID("2306066404030351200248e1e9c96ff1");
        Map<String, Integer> dataMap = new HashMap<>();
        dataMap.put("onoff",1);
        dataMap.put("channel",0);
        Map<String, Object> togglexMap = new HashMap<>();
        togglexMap.put("togglex",dataMap);
        String payload=new Gson().toJson(togglexMap);
        String mqttMessage = MerossMqttConnector.buildMqttMessage("SET", MerossConstants
                .Namespace.CONTROL_TOGGLEX.getValue(), payload,MerossMqttConnector.buildResponseTopic());
        byte[] decodedBytes = Base64.getDecoder().decode(mqttMessage);
        String decodedString = new String(decodedBytes);
        logger.info(decodedString);
        assertNotNull(decodedString);
    }
}
