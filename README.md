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

