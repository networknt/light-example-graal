In the light platform, we have configuration directory passed to the server instance with a system property in the command line. Let's see if we can do that same to pass the system properties into the native applcaiton. 

### Copy from logback

To create this project, it is easy to copy from the logback example and make the following modifications. 

* update the pom.xml to change the artifact and name to prop
* remove the dependency for slf4j and logback.
* change the package name to com.networknt.prop
* remove the logging imports and statements from the App.java

### App.java

```
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
```

### JVM Run

```
steve@freedom:~/networknt/light-example-graal/system-properties$ mvn clean install
steve@freedom:~/networknt/light-example-graal/system-properties$ java -jar target/prop-1.0.jar 
configDir = null
steve@freedom:~/networknt/light-example-graal/system-properties$ java -Dlight-4j-config-dir=/tmp/config -jar target/prop-1.0.jar 
configDir = /tmp/config
```

If you have the command line option, then the value is capture in the application as expected. 

### native-image

```
steve@freedom:~/networknt/light-example-graal/system-properties$ native-image --static --no-server -jar target/prop-1.0.jar
[prop-1.0:26863]    classlist:   1,112.60 ms
[prop-1.0:26863]        (cap):     915.29 ms
[prop-1.0:26863]        setup:   1,783.83 ms
[prop-1.0:26863]   (typeflow):   1,803.76 ms
[prop-1.0:26863]    (objects):     618.26 ms
[prop-1.0:26863]   (features):     105.85 ms
[prop-1.0:26863]     analysis:   2,576.11 ms
[prop-1.0:26863]     universe:     173.83 ms
[prop-1.0:26863]      (parse):     214.52 ms
[prop-1.0:26863]     (inline):     690.09 ms
[prop-1.0:26863]    (compile):   2,122.66 ms
[prop-1.0:26863]      compile:   3,253.46 ms
[prop-1.0:26863]        image:     356.61 ms
[prop-1.0:26863]        write:     141.04 ms
[prop-1.0:26863]      [total]:   9,512.24 ms
steve@freedom:~/networknt/light-example-graal/system-properties$ ls -la
total 3848
drwxr-xr-x  4 steve steve    4096 Apr 19 22:18 .
drwxr-xr-x 10 steve steve    4096 Apr 19 21:49 ..
-rw-r--r--  1 steve steve      80 Apr 19 22:07 .gitignore
-rw-r--r--  1 steve steve    1716 Apr 19 22:02 pom.xml
-rwxr-xr-x  1 steve steve 3910504 Apr 19 22:18 prop-1.0
-rw-r--r--  1 steve steve    1413 Apr 19 22:16 README.md
drwxr-xr-x  4 steve steve    4096 Apr 19 21:59 src
drwxr-xr-x  9 steve steve    4096 Apr 19 22:14 target
steve@freedom:~/networknt/light-example-graal/system-properties$ ./prop-1.0 -Dlight-4j-config-dir=/tmp/config
configDir = /tmp/config
```

Perfect. The native executable works flawlessly. This means we can pass into the application system properties just as normal Java application running in the JVM. 


### Reference

I found this issue after I created this example. It contains more information than this example. 

https://github.com/oracle/graal/issues/779



