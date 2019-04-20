To use native-image to compile the application into native executables, we need to handle the logging gracefully. Let's first create new project in Maven. 

### Create Project

```
cd ~/networknt/light-example/graal
mvn archetype:generate -DgroupId=com.networknt.logback -DartifactId=logback -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

let's change the pom.xml with the following 

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.networknt.logback</groupId>
  <artifactId>logback</artifactId>
  <packaging>jar</packaging>
  <version>1.0</version>
  <name>logback</name>
  <url>http://maven.apache.org</url>
  <properties>
    <!-- https://maven.apache.org/general.html#encoding-warning -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.3</version>
    </dependency>
    <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>1.7.25</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.12</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-shade-plugin</artifactId>
         <version>3.2.0</version>
         <executions>
          <!-- Attach the shade into the package phase -->
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
            <configuration>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                  <mainClass>com.networknt.logback.App</mainClass>
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>

```

And make some changes to the generated App.java

```
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
        logger.debug("Debug");
        logger.info("Info");
        logger.error("Error");
    }
}

```

### Build and Run

As you can see, we output one line with System.out and three lines with logback on the console. Use the following commands to build and run the application.

```
steve@freedom:~/networknt/light-example-graal/logback$ mvn clean install
freedom:~/networknt/light-example-graal/logback$ java -jar target/logback-1.0.jar 
Hello World!
23:55:30.697 [main] DEBUG com.networknt.logback.App - Debug
23:55:30.699 [main] INFO com.networknt.logback.App - Info
23:55:30.699 [main] ERROR com.networknt.logback.App - Error
```

### native-image

Let's compile the jar with native-image.

```
steve@freedom:~/networknt/light-example-graal/logback$ native-image --static --no-server -jar target/logback-1.0.jar
[logback-1.0-SNAPSHOT:11057]    classlist:   1,354.59 ms
[logback-1.0-SNAPSHOT:11057]        (cap):   1,043.27 ms
[logback-1.0-SNAPSHOT:11057]        setup:   1,885.44 ms
[logback-1.0-SNAPSHOT:11057]   (typeflow):   4,606.20 ms
[logback-1.0-SNAPSHOT:11057]    (objects):   1,942.52 ms
[logback-1.0-SNAPSHOT:11057]   (features):     120.98 ms
[logback-1.0-SNAPSHOT:11057]     analysis:   6,763.98 ms
[logback-1.0-SNAPSHOT:11057]     universe:     255.10 ms
[logback-1.0-SNAPSHOT:11057]      (parse):     418.33 ms
[logback-1.0-SNAPSHOT:11057]     (inline):   1,112.73 ms
[logback-1.0-SNAPSHOT:11057]    (compile):   4,125.51 ms
[logback-1.0-SNAPSHOT:11057]      compile:   6,069.00 ms
[logback-1.0-SNAPSHOT:11057]        image:     550.00 ms
[logback-1.0-SNAPSHOT:11057]        write:     184.61 ms
[logback-1.0-SNAPSHOT:11057]      [total]:  17,183.54 ms
steve@freedom:~/networknt/light-example-graal/logback$ ls -l
total 8800
-rw-r--r-- 1 steve steve    1687 Apr 19 23:55 dependency-reduced-pom.xml
-rwxr-xr-x 1 steve steve 8958824 Apr 19 23:56 logback-1.0
-rw-r--r-- 1 steve steve    1233 Apr 19 17:30 logback.iml
-rw-r--r-- 1 steve steve     526 Apr 19 23:53 logback.xml
-rw-r--r-- 1 steve steve    2024 Apr 19 23:54 pom.xml
-rw-rw-r-- 1 steve steve   14553 Apr 19 23:56 README.md
-rw-rw-r-- 1 steve steve    4652 Apr 19 19:27 reflection.json
drwxr-xr-x 4 steve steve    4096 Apr 19 17:16 src
drwxr-xr-x 9 steve steve    4096 Apr 19 23:55 target
steve@freedom:~/networknt/light-example-graal/logback$ ./logback-1.0 
Hello World!
23:58:12.557 [main] DEBUG com.networknt.logback.App - Debug
23:58:12.557 [main] INFO com.networknt.logback.App - Info
23:58:12.557 [main] ERROR com.networknt.logback.App - Error
```

It works without any problem. 

### Logging Level

Let's see if we can pass in a loglevel.xml to change the logging level to error from default debug. 

loglevel.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %X{sId} %X{cId} %-5level %logger{36} %M - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.networknt" level="error" additivity="false">
        <appender-ref ref="stdout"/>
    </logger>

</configuration>

```

To run it in JVM. 

```
steve@freedom:~/networknt/light-example-graal/logback$ java -Dlogback.configurationFile=loglevel.xml -jar target/logback-1.0.jar 
Hello World!
00:06:34.097 [main]   ERROR com.networknt.logback.App main - Error
```

If works as there is only one line output from logback with ERROR level. 

To run it in native.

```
steve@freedom:~/networknt/light-example-graal/logback$ ./logback-1.0 -Dlogback.configurationFile=loglevel.xml
Hello World!
00:08:17.963 [main] DEBUG com.networknt.logback.App - Debug
00:08:17.963 [main] INFO com.networknt.logback.App - Info
00:08:17.963 [main] ERROR com.networknt.logback.App - Error

```

There are three lines output from logback which means that loglevel.xml is not used. It looks like the -D system properties are not working for logback. 

### Log File

In the next step, let's add a logback.xml and try to write the log into a log file. 

logback.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="log" class="ch.qos.logback.core.FileAppender">
        <File>log/test.log</File>
        <Append>false</Append>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %X{sId} %X{cId} %-5level %class{36}:%L %M - %msg%n</Pattern>
        </layout>
    </appender>

    <logger name="com.networknt" level="debug" additivity="false">
        <appender-ref ref="log"/>
    </logger>

</configuration>
```

Let's execute the application with logback config file in JVM to make sure it works. 

```
steve@freedom:~/networknt/light-example-graal/logback$ java -Dlogback.configurationFile=logback.xml -jar target/logback-1.0-SNAPSHOT.jar 
18:10:18,034 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/home/steve/networknt/light-example-graal/logback/logback.xml]
18:10:18,084 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - debug attribute not set
18:10:18,084 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.FileAppender]
18:10:18,089 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [log]
18:10:18,125 |-WARN in ch.qos.logback.core.FileAppender[log] - This appender no longer admits a layout as a sub-component, set an encoder instead.
18:10:18,125 |-WARN in ch.qos.logback.core.FileAppender[log] - To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.
18:10:18,125 |-WARN in ch.qos.logback.core.FileAppender[log] - See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details
18:10:18,125 |-INFO in ch.qos.logback.core.FileAppender[log] - File property is set to [log/test.log]
18:10:18,126 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [com.networknt] to DEBUG
18:10:18,126 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [com.networknt] to false
18:10:18,126 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [log] to Logger[com.networknt]
18:10:18,126 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
18:10:18,127 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@68f7aae2 - Registering current configuration as safe fallback point

Hello World!
steve@freedom:~/networknt/light-example-graal/logback$ ls -l
total 8788
-rw-r--r-- 1 steve steve    1696 Apr 19 17:30 dependency-reduced-pom.xml
drwxr-xr-x 2 steve steve    4096 Apr 19 18:10 log
-rwxr-xr-x 1 steve steve 8958832 Apr 19 17:38 logback-1.0-SNAPSHOT
-rw-r--r-- 1 steve steve    1233 Apr 19 17:30 logback.iml
-rw-r--r-- 1 steve steve     526 Apr 19 18:07 logback.xml
-rw-r--r-- 1 steve steve    2033 Apr 19 17:26 pom.xml
-rw-rw-r-- 1 steve steve    5698 Apr 19 18:08 README.md
drwxr-xr-x 4 steve steve    4096 Apr 19 17:16 src
drwxr-xr-x 9 steve steve    4096 Apr 19 17:30 target
steve@freedom:~/networknt/light-example-graal/logback$ cd log
steve@freedom:~/networknt/light-example-graal/logback/log$ ls
test.log
steve@freedom:~/networknt/light-example-graal/logback/log$ more test.log
18:10:18.129 [main]   DEBUG com.networknt.logback.App:16 main - Hello World!

```

Now, let's see if the externalized logback.xml works for the native application.

```
steve@freedom:~/networknt/light-example-graal/logback$ rm -rf log
steve@freedom:~/networknt/light-example-graal/logback$ ./logback-1.0 -Dlogback.configurationFile=logback.xml
Hello World!
18:14:12.952 [main] DEBUG com.networknt.logback.App - Hello World!
steve@freedom:~/networknt/light-example-graal/logback$ ls -l
total 8788
-rw-r--r-- 1 steve steve    1696 Apr 19 17:30 dependency-reduced-pom.xml
-rwxr-xr-x 1 steve steve 8958832 Apr 19 17:38 logback-1.0-SNAPSHOT
-rw-r--r-- 1 steve steve    1233 Apr 19 17:30 logback.iml
-rw-r--r-- 1 steve steve     526 Apr 19 18:07 logback.xml
-rw-r--r-- 1 steve steve    2033 Apr 19 17:26 pom.xml
-rw-rw-r-- 1 steve steve    8448 Apr 19 18:12 README.md
drwxr-xr-x 4 steve steve    4096 Apr 19 17:16 src
drwxr-xr-x 9 steve steve    4096 Apr 19 17:30 target

```

It doesn't work. There is no log directory created and the logging statement is still displayed on the console.

When using docker, it is OK as the console log can be captured and forwarded to the ElasticSearch. In the Kubernetes cluster, it is the same that console log can be captured. 

Let's try to run native-image with the option. 

```
steve@freedom:~/networknt/light-example-graal/logback$ native-image --static --no-server -jar target/logback-1.0.jar -Dlogback.configurationFile=logback.xml
[logback-1.0-SNAPSHOT:14602]    classlist:   1,235.83 ms
[logback-1.0-SNAPSHOT:14602]        (cap):     888.37 ms
[logback-1.0-SNAPSHOT:14602]        setup:   1,765.49 ms
19:22:58,968 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/home/steve/networknt/light-example-graal/logback/logback.xml]
19:22:59,006 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - debug attribute not set
19:22:59,007 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.FileAppender]
19:22:59,007 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [log]
19:22:59,019 |-WARN in ch.qos.logback.core.FileAppender[log] - This appender no longer admits a layout as a sub-component, set an encoder instead.
19:22:59,019 |-WARN in ch.qos.logback.core.FileAppender[log] - To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.
19:22:59,019 |-WARN in ch.qos.logback.core.FileAppender[log] - See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details
19:22:59,019 |-INFO in ch.qos.logback.core.FileAppender[log] - File property is set to [log/test.log]
19:22:59,020 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [com.networknt] to DEBUG
19:22:59,020 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [com.networknt] to false
19:22:59,020 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [log] to Logger[com.networknt]
19:22:59,020 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
19:22:59,020 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@7d001e3b - Registering current configuration as safe fallback point

[logback-1.0-SNAPSHOT:14602]     analysis:   5,038.02 ms
Warning: Abort stand-alone image build. Detected a FileDescriptor in the image heap. File descriptors opened during image generation are no longer open at image run time, and the files might not even be present anymore at image run time. The object was probably created by a class initializer and is reachable from a static field. By default, all class initialization is done during native image building.You can manually delay class initialization to image run time by using the option --delay-class-initialization-to-runtime=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Trace: 	object java.io.FileOutputStream
	object ch.qos.logback.core.recovery.ResilientFileOutputStream
	object ch.qos.logback.core.FileAppender
	object ch.qos.logback.core.status.InfoStatus
	object java.lang.Object[]
	object java.util.ArrayList
	object ch.qos.logback.core.BasicStatusManager
	object ch.qos.logback.classic.LoggerContext
	object ch.qos.logback.classic.Logger
	field com.networknt.logback.App.logger

Build on Server(pid: 14738, port: 33371)*
[logback-1.0-SNAPSHOT:14738]    classlist:   1,087.08 ms
[logback-1.0-SNAPSHOT:14738]        (cap):     936.60 ms
[logback-1.0-SNAPSHOT:14738]        setup:   1,814.98 ms
[logback-1.0-SNAPSHOT:14738]   (typeflow):   1,904.68 ms
[logback-1.0-SNAPSHOT:14738]    (objects):     641.32 ms
[logback-1.0-SNAPSHOT:14738]   (features):     146.23 ms
[logback-1.0-SNAPSHOT:14738]     analysis:   2,750.05 ms
[logback-1.0-SNAPSHOT:14738]     universe:     156.77 ms
[logback-1.0-SNAPSHOT:14738]      (parse):     313.31 ms
[logback-1.0-SNAPSHOT:14738]     (inline):     703.37 ms
[logback-1.0-SNAPSHOT:14738]    (compile):   2,177.64 ms
[logback-1.0-SNAPSHOT:14738]      compile:   3,457.72 ms
[logback-1.0-SNAPSHOT:14738]        image:     324.41 ms
[logback-1.0-SNAPSHOT:14738]        write:     110.12 ms
[logback-1.0-SNAPSHOT:14738]      [total]:   9,806.08 ms
Warning: Image 'logback-1.0-SNAPSHOT' is a fallback-image

```

It looks like we need to define the reflection.json for the logback. 

reflection.json

```
native-image -H:ReflectionConfigurationFiles=reflection.json --delay-class-initialization-to-runtime=ch.qos.logback.classic.Logger --static --no-server -jar target/logback-1.0.jar -Dlogback.configurationFile=logback.xml

native-image -H:ReflectionConfigurationFiles=reflection.json --static --no-server -jar target/logback-1.0.jar -Dlogback.configurationFile=logback.xml

```

We still have the same problem. After a search on the Internet, I was led to this url. 

https://medium.com/graalvm/understanding-class-initialization-in-graalvm-native-image-generation-d765b7e4d6ed


