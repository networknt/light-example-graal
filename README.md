# light-example-graal
Native Java Applications with GraalVM

For applications built on top of light-4j, they start very fast and use less memory than most services running in the JVM. With GraalVM native-image compiler, we can compile the Java application into native executables and reduce the startup time and memory footprint further. Although there are a lot of benefits to adopt GraalVM, a lot of limiations must be considered before compiling the light-4j service into a native executable. 

While learning the GraalVM, I have created a lot of examples to try out differnt limitations of native-image tool. 

### hello-java

This is a very simple Hello World example implemented in Java. For more info about this example, please visit [hello-java](hello-java)

### hello-kotlin

Some users are building application in kotlin, we need to make sure that services built with Kotlin can be compiled into native executables. For more info about this example, please visit [hello-kotlin](hello-kotlin)

### reflection

The light-4j service module uses reflection to handle dependency injection. We need to make sure that native-image tool can handle the reflection with a config file. For more details, please visit [reflection](reflection)

### service-loader

If you are using Java ServiceLoader to inject implementations for an interface, please visit [service-loader](service-loader)

### system-properties

For light-4j config, we need to pass the System Properties to the application during the runtime to change the behavior of the service. This example is use to demo the native executable with command line system properties. For more info, please visit [system-properties](system-properties)

### logback

This is the example that explores logback in the native executable. Most importantly, how to pass the config file to change the logging level by restarting the server. For more details, please visit [logback](logback)

### util-logging

As we found some issues with logback, this example explores java logging as it is supported by GraalVM. For more details, please visit [util-logging](util-logging)

