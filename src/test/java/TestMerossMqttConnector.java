import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossConstants;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        String payload = """
                        {'togglex': {"onoff": 1, "channel": 0}}""";
        String mqttMessage = MerossMqttConnector.buildMqttMessage("SET", MerossConstants
                .Namespace.CONTROL_TOGGLEX.getValue(), payload,MerossMqttConnector.buildResponseTopic());
        logger.info(mqttMessage);
        assertNotNull(mqttMessage);
    }
}
