package org.meross4j.factory;

public class FactoryProvider {
    public FactoryProvider() {}
    public static AbstractFactory getFactory(String commandType) {
        return switch (commandType){
            case "CONTROL_TOGGLEX" -> new TogglexFactory();
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }
}
