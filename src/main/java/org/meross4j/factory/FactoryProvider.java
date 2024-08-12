package org.meross4j.factory;

public class FactoryProvider {
    public FactoryProvider() {}
    public static AbstractFactory getFactory(String devType) {
        return switch (devType){
            case "mss110","mss210","mss310","mss310h" -> new TogglexFactory();
            default -> throw new IllegalStateException("Unexpected value: " + devType);
        };
    }
}
