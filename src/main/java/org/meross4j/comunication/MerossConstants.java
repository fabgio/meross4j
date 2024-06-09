package org.meross4j.comunication;

public class MerossConstants {
    //endpoints
    public static final String LOGIN_PATH = "/v1/Auth/signIn";
    public static final String DEV_LIST_PATH = "/v1/Device/devList";

    public enum Namespace{
        // Common abilities
        SYSTEM_ALL("Appliance.System.All"),
        SYSTEM_ABILITY("Appliance.System.Ability"),
        SYSTEM_ONLINE("Appliance.System.Online"),
        SYSTEM_REPORT("Appliance.System.Report"),
        SYSTEM_DEBUG("Appliance.System.Debug"),
        SYSTEM_RUNTIME("Appliance.System.Runtime"),

        SYSTEM_ENCRYPTION("Appliance.Encrypt.Suite"),
        SYSTEM_ENCRYPTION_ECDHE("Appliance.Encrypt.ECDHE"),

        CONTROL_BIND("Appliance.ControlBind"),
        CONTROL_CONTROL_UNBIND("Appliance.Control.Unbind"),
        CONTROL_TRIGGER("Appliance.Control.Trigger"),
        CONTROL_TRIGGERX("Appliance.Control.TriggerX"),

        CONFIG_WIFI_LIST("Appliance.Config.WifiList"),
        CONFIG_TRACE("Appliance.Config.Trace"),
        SYSTEM_DND_MODE("Appliance.System.DND.Mode"),

        // Power bulb/plug capabilities

        CONTROL_TOGGLE("Appliance.Control.Toggle"),
        CONTROL_TOGGLEX("Appliance.Control.ToggleX"),
        CONTROL_ELECTRICITY("Appliance.Control.Electricity"),
        CONTROL_CONSUMPTION("Appliance.Control.Consumption"),
        CONTROL_CONSUMPTIONX("Appliance.Control.ConsumptionX"),

        // Bulb only abilities

        CONTROL_LIGHT("Appliance.Control.Light"),

        // Garage opener abilities

        GARAGE_DOOR_STATE("Appliance.GarageDoor.State"),
        GARAGE_DOOR_MULTIPLE_CONFIG("Appliance.GarageDoor.MultipleConfig"),

        // Roller shutter timer

        ROLLER_SHUTTER_STATE("Appliance.Roller.State"),
        ROLLER_SHUTTER_POSITION("Appliance.Roller.Position"),
        ROLLER_SHUTTER_CONFIG("Appliance.Roller.Config"),

        // Humidifier

        CONTROL_SPRAY("Appliance.Control.Spray"),
        SYSTEM_DIGEST_HUB("Appliance.System.Digest.Hub"),

        // Oil diffuser

        DIFFUSER_LIGHT("Appliance.Control.Diffuser.Light"),
        DIFFUSER_SPRAY("Appliance.Control.Diffuser.Spray"),

        // Hub

        HUB_EXCEPTION("Appliance.Hub.Exception"),
        HUB_BATTERY("Appliance.Hub.Battery"),
        HUB_TOGGLEX("Appliance.Hub.ToggleX"),
        HUB_ONLINE("Appliance.Hub.Online"),
        HUB_SUBDEVICE_LIST("Appliance.Hub.SubdeviceList"),

        // Sensors

        HUB_SENSOR_ALL("Appliance.Hub.Sensor.All"),
        HUB_SENSOR_TEMPHUM("Appliance.Hub.Sensor.TempHum"),
        HUB_SENSOR_ALERT("Appliance.Hub.Sensor.Alert"),

        // MTS 100

        HUB_MTS100_ALL("Appliance.Hub.Mts100.All"),
        HUB_MTS100_TEMPERATURE("Appliance.Hub.Mts100.Temperature"),
        HUB_MTS100_ADJUST("Appliance.Hub.Mts100.Adjust"),

        // Thermostat / MTS200

        CONTROL_THERMOSTAT_MODE("Appliance.Control.Thermostat.Mode"),
        CONTROL_THERMOSTAT_WINDOWOPENED("Appliance.Control.Thermostat.WindowOpened");

        private String value;


        Namespace(String value) {
            this.value = value;
        }
    }
}
