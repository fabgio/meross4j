package org.meross4j.factory;

public class FactoryProvider {
    public FactoryProvider() {}
    public static AbstractFactory getFactory(String type) {
        return switch (type){
            case "mss110","mss210","mss310","mss310h" -> new MSSFactory();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
