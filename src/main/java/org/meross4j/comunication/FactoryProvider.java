package org.meross4j.comunication;

public class FactoryProvider {
    private FactoryProvider() {}
    static AbstractFactory getFactory(String type) {
        return switch (type){
            case "ms210" ->new ToggleXFactory();
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
