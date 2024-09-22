# meross4j

**meross4j** is a Java library providing API for controlling Meross IoT devices over the internet.

To utilize the library you should first create an account via The Meross android app. Moreover, the devices should be in an 
online status.

Supported capabilities:
* `Togglex` Power bulb/plug capability 

### TODO

- [ ] add Consumptionx capability

## Dependency Management

### Maven

This library is available via Maven Central repository by adding the dependency in your POM.xml:



```xml   

    <dependency>
      <groupId>org.meross4j</groupId>
      <artifactId>meross4j</artifactId>
      <version>0.2</version>
    </dependency>
```

## Usage example
```java 
// Creates  HttpConnection with EU URL = https://iotx-eu.meross.com"; email = myemail@email.com  and password = mypassword
MerossHttpConnector merossHttpConnector = new MerossHttpConnector("https://iotx-eu.meross.com", "myemail@email.com" , "mypassword");
var manager = MerossManager.createMerossManager(merossHttpConnector);
// Turns smart plug named "Desk" ON
var response = manager.executeCommand("Desk",
        MerossEnum.Namespace.CONTROL_TOGGLEX.name(),"ON");

// Turns smart plug named "Desk" OFF
var response =  manager.executeCommand("Desk",
        MerossEnum.Namespace.CONTROL_TOGGLEX.name(),"OFF");

// Get info about  "Desk" e.g. current ON/OFF state ...  
var response =  manager.executeCommand("Desk",
        MerossEnum.Namespace.CONTROL_TOGGLEX.name());

```

## Currently supported devices

Tested devices so far:

- MSS210 (Smart plug)

Anyway the library may support the togglex capability, i.e. ON/OFF for the majority of plugs/bulbs

## Building from Source

With Maven:

```
mvn clean install
```

## Contributions
Pull requests are really welcomed

## Disclaimer
This library is not associated by any means with Meross or other subsidiaries

