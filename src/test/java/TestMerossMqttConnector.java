import org.junit.jupiter.api.Test;
import org.meross4j.comunication.MerossMqttConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMerossMqttConnector {
    private final static Logger logger = LoggerFactory.getLogger(TestMerossMqttConnector.class);
    @Test
    void testBuildResponseTopicIsNotNull(){
        MerossMqttConnector.userId="3807527";
        String responseTopic = MerossMqttConnector.buildResponseTopic();
        logger.info(responseTopic);
        assertNotNull(responseTopic);
    }
    @Test
    void testBuildToggleXMessage(){
        String method = "SET";
        String nameSpace = "Namespace.CONTROL_TOGGLEX";
        String payload = """
                        {'togglex': {"onoff": 0, "channel": 0}}""";
       String mqttMessage = MerossMqttConnector.buildMqttMessage(method,nameSpace,payload);
       logger.info(mqttMessage);
       assertNotNull(mqttMessage);
    }
}
