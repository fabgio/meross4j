import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
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

    
    @Test
    void testBuildToggleXMessage() {
        MerossMqttConnector.setUserId("3807527");
        Map<String,Object> payload = Map.of("togglex",Map.of("onoff",1,"channel",0));
        ByteBuffer mqttMessage = ByteBuffer.wrap(MerossMqttConnector.buildMqttMessage("SET", MerossConstants
                .Namespace.CONTROL_TOGGLEX.getValue(), payload));
        CharBuffer charBuffer= StandardCharsets.UTF_8.decode(mqttMessage);
        logger.info("MQTT Message : {}", charBuffer);
        assertNotNull(charBuffer);
    }
}

