import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossMqttConnector.class);
    @Test
    void testBuildResponseTopicIsNotNull(){
        MerossMqttConnector.setUserId("3807527");
        String responseTopic = MerossMqttConnector.buildClientResponseTopic();
        logger.info("Response Topic:{} ",responseTopic);
        assertNotNull(responseTopic);
    }

    @Test
    void testBuildClientUserTopicIsNotNull() {
        MerossMqttConnector.setUserId("3807527");
        String clientUserTopic = MerossMqttConnector.buildClientUserTopic();
        logger.info("Client-User Topic:{} ", clientUserTopic);
        assertNotNull(clientUserTopic);
    }
    @Disabled
    @Test
    void testBuildToggleXMessage(){
        MerossMqttConnector.setUserId("3807527");
        String payload = """
                        {'togglex': {"onoff": 1, "channel": 0}}""";
        String mqttMessage = MerossMqttConnector.buildMqttMessage("SET", MerossConstants
                .Namespace.CONTROL_TOGGLEX.getValue(), payload);
        byte[] decodedBytes = Base64.getDecoder().decode(mqttMessage);
        String decodedString = new String(decodedBytes);
        logger.info(decodedString);
        assertNotNull(decodedString);
    }
}
