package com.networknt.prop;

/**
 * We are expecting -Dlight-4j-config-dir=String to pass in the command line.
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Object configDir = System.getProperties().get("light-4j-config-dir");
        System.out.println("configDir = " + configDir);
    }
}
