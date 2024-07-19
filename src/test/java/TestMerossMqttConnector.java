import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossMqttConnector.class);

    @Test
    void testBuildResponseTopicIsNotNull() {
        MerossMqttConnector.setUserId("3807527");
        String responseTopic = MerossMqttConnector.buildClientResponseTopic();
        logger.info("Response Topic:  {} ", responseTopic);
        assertNotNull(responseTopic);
    }

    @Test
    void testBuildClientUserTopicIsNotNull() {
        MerossMqttConnector.setUserId("3807527");
        String clientUserTopic = MerossMqttConnector.buildClientUserTopic();
        logger.info("Client-User Topic:  {} ", clientUserTopic);
        assertNotNull(clientUserTopic);
    }

    @Test
    void testBuildClientUIdsNotNull() {
        MerossMqttConnector.setUserId("3807527");
        String clientIfd = MerossMqttConnector.buildClientId();
        logger.info("ClientId:  {} ", clientIfd);
        assertNotNull(clientIfd);
    }

    @Disabled
    @Test
    void testBuildToggleXMessage() {
        MerossMqttConnector.setUserId("3807527");
        Map<String, Integer> elements = new LinkedHashMap<>();
        elements.put("onoff", 0);
        elements.put("channel", 0);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("togglex", elements);
        var mqttMessage = MerossMqttConnector.buildMqttMessage("SET", MerossConstants
                .Namespace.CONTROL_TOGGLEX.getValue(), payload);
        logger.info("MQTT Message : {}", mqttMessage);
        assertNotNull(mqttMessage);
    }
}

