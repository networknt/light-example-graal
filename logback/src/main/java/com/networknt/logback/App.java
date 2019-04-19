package com.networknt.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    static Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        logger.debug("Hello World!");
    }
}
