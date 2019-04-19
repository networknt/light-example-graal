To use native-image to compile the application into native executables, we need to handle the logging gracefully. Let's first create new project in Maven. 

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
  <version>1.0-SNAPSHOT</version>
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
        logger.debug("Hello World!");
    }
}

```

As you can see, we output one line with System.out and another line with logback on the console. Use the following commands to build and run the application.

```
steve@freedom:~/networknt/light-example-graal/logback$ mvn clean install
steve@freedom:~/networknt/light-example-graal/logback$ java -jar target/logback-1.0-SNAPSHOT.jar 
Hello World!
17:30:55.143 [main] DEBUG com.networknt.logback.App - Hello World!
```

Let's compile the jar with native-image.

```
steve@freedom:~/networknt/light-example-graal/logback$ native-image --static --no-server -jar target/logback-1.0-SNAPSHOT.jar
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
total 8776
-rw-r--r-- 1 steve steve    1696 Apr 19 17:30 dependency-reduced-pom.xml
-rwxr-xr-x 1 steve steve 8958832 Apr 19 17:38 logback-1.0-SNAPSHOT
-rw-r--r-- 1 steve steve    1233 Apr 19 17:30 logback.iml
-rw-r--r-- 1 steve steve    2033 Apr 19 17:26 pom.xml
-rw-rw-r-- 1 steve steve    3262 Apr 19 17:35 README.md
drwxr-xr-x 4 steve steve    4096 Apr 19 17:16 src
drwxr-xr-x 9 steve steve    4096 Apr 19 17:30 target
steve@freedom:~/networknt/light-example-graal/logback$ ./logback-1.0-SNAPSHOT 
Hello World!
17:39:40.060 [main] DEBUG com.networknt.logback.App - Hello World!
```

It works without any problem. 

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
steve@freedom:~/networknt/light-example-graal/logback$ ./logback-1.0-SNAPSHOT -Dlogback.configurationFile=logback.xml
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



