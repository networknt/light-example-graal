In the light platform, we have service module that responsible for IoC or dependency injection to support plugins in the platform. It is based on the service.yml configuration file and heavily use the Java reflection to create the singleton instances for the interfaces defined in the config. 

In this example, let' explore how the GraalVM handles the reflection, especially the constructor injection. I want to learn the basics before diving into the light-4j service module. 

In most of the cases, there is an interface and implementation map in the service.yml and we need to create an instance of the implementation with Class.forName. Given that the configuration is externalized, there is no constant for the GraalVM to automatic detection. 

Let's create a small example to do just that. 

ReflectionDemo.java

```
public class ReflectionDemo {

   public static void main(String[] args) {

      try {
         // returns the Class object for the class with the specified name
         Class cls = Class.forName(args[0]);
         // returns the name and package of the class
         System.out.println("Class found = " + cls.getName());
         System.out.println("Package = " + cls.getPackage());

         // create an instance of the cls. 
         // This requires that the class has a default constructor
         Object obj = cls.getConstructor().newInstance();
         System.out.println("Object created = " + obj);
      } catch(Exception ex) {
         System.out.println(ex.toString());
      }
   }
}
```

It is a very simple class that accepts a parameter of class name, creates the Class object and instanciate an instance of the class with the public default constructor. 

In the JDK, there are so many classes that have a default constructor. For example, java.util.HashMap or java.util.ArrayList, etc.

Let's compile the class and run it. 

```
javac ReflectionDemo.java
```

With java.util.HashMap as input.

```
steve@freedom:~/networknt/light-example-graal/reflection$ java ReflectionDemo java.util.HashMap
Class found = java.util.HashMap
Package = package java.util, Java Platform API Specification, version 1.8
Object created = {}
```

With java.util.ArrayList as input.

```
steve@freedom:~/networknt/light-example-graal/reflection$ java ReflectionDemo java.util.ArrayList
Class found = java.util.ArrayList
Package = package java.util, Java Platform API Specification, version 1.8
Object created = []
```

As we can see that the application works well in the JVM. 

Now let's build a native image and run the application again. 

```
steve@freedom:~/networknt/light-example-graal/reflection$ native-image ReflectionDemo
Build on Server(pid: 19176, port: 34583)*
[reflectiondemo:19176]    classlist:   1,082.19 ms
[reflectiondemo:19176]        (cap):   1,035.38 ms
[reflectiondemo:19176]        setup:   1,911.41 ms
[reflectiondemo:19176]   (typeflow):   4,019.23 ms
[reflectiondemo:19176]    (objects):   1,244.97 ms
[reflectiondemo:19176]   (features):     161.49 ms
[reflectiondemo:19176]     analysis:   5,501.47 ms
[reflectiondemo:19176]     universe:     183.05 ms
[reflectiondemo:19176]      (parse):     219.22 ms
[reflectiondemo:19176]     (inline):     868.55 ms
[reflectiondemo:19176]    (compile):   2,587.65 ms
[reflectiondemo:19176]      compile:   4,106.01 ms
[reflectiondemo:19176]        image:     679.25 ms
[reflectiondemo:19176]        write:      73.25 ms
[reflectiondemo:19176]      [total]:  13,636.93 ms
steve@freedom:~/networknt/light-example-graal/reflection$ ls -l
total 3344
-rw-r--r-- 1 steve steve    2396 Apr 19 09:29 README.md
-rwxr-xr-x 1 steve steve 3410232 Apr 19 09:30 reflectiondemo
-rw-r--r-- 1 steve steve    1218 Apr 19 09:10 ReflectionDemo.class
-rw-rw-r-- 1 steve steve     703 Apr 19 09:23 ReflectionDemo.java
steve@freedom:~/networknt/light-example-graal/reflection$ ./reflectiondemo java.util.HashMap
java.lang.ClassNotFoundException: java.util.HashMap

```

The application can be compiled but didn't run successfully. It throws an ClassNotFoundException during the runtime. 

We need to let the GraalVM know that we are going to use reflection for java.util.HashMap and java.util.ArrayList. 

Let's create a refection.json with the following content.

```
[
  {
    "name" : "java.util.HashMap",
    "allDeclaredConstructors" : true,
    "allPublicConstructors" : true,
    "allDeclaredMethods" : true,
    "allPublicMethods" : true,
    "allDeclaredClasses" : true,
    "allPublicClasses" : true
  },
  {
    "name" : "java.util.ArrayList",
    "allDeclaredConstructors" : true,
    "allPublicConstructors" : true,
    "allDeclaredMethods" : true,
    "allPublicMethods" : true,
    "allDeclaredClasses" : true,
    "allPublicClasses" : true
  }
]
```

Let's recompile the application with native-image.

```
steve@freedom:~/networknt/light-example-graal/reflection$ native-image -H:ReflectionConfigurationFiles=reflection.json --static --no-server ReflectionDemo
[reflectiondemo:20648]    classlist:   1,101.14 ms
[reflectiondemo:20648]        (cap):     939.38 ms
[reflectiondemo:20648]        setup:   1,814.03 ms
[reflectiondemo:20648]   (typeflow):   3,605.21 ms
[reflectiondemo:20648]    (objects):   1,321.39 ms
[reflectiondemo:20648]   (features):     137.35 ms
[reflectiondemo:20648]     analysis:   5,132.13 ms
[reflectiondemo:20648]     universe:     161.78 ms
[reflectiondemo:20648]      (parse):     213.77 ms
[reflectiondemo:20648]     (inline):     842.71 ms
[reflectiondemo:20648]    (compile):   2,683.00 ms
[reflectiondemo:20648]      compile:   3,990.30 ms
[reflectiondemo:20648]        image:     330.17 ms
[reflectiondemo:20648]        write:     160.83 ms
[reflectiondemo:20648]      [total]:  12,813.37 ms
steve@freedom:~/networknt/light-example-graal/reflection$ ls -la
total 4852
drwxr-xr-x 2 steve steve    4096 Apr 19 09:45 .
drwxr-xr-x 6 steve steve    4096 Apr 19 08:48 ..
-rw-r--r-- 1 steve steve    4737 Apr 19 09:42 README.md
-rwxr-xr-x 1 steve steve 4939648 Apr 19 09:45 reflectiondemo
-rw-r--r-- 1 steve steve    1218 Apr 19 09:10 ReflectionDemo.class
-rw-rw-r-- 1 steve steve     703 Apr 19 09:23 ReflectionDemo.java
-rw-rw-r-- 1 steve steve     493 Apr 19 09:42 reflection.json
steve@freedom:~/networknt/light-example-graal/reflection$ ./reflectiondemo java.util.HashMap
Class found = java.util.HashMap
Package = package java.util
Object created = {}
steve@freedom:~/networknt/light-example-graal/reflection$ ./reflectiondemo java.util.ArrayList
Class found = java.util.ArrayList
Package = package java.util
Object created = []

```

As you can see, the executable is almost 4.9MB instead of 3.4MB last time. Anyway, it works perfectly this time. 

In a real service, it is very hard for developers to create this reflection configuration manually. The GraalVM provide a tool that you can record the classes used in reflection with an agent for the JVM. 

```
java -agentlib:native-image-agent=config-output-dir=config ReflectionDemo java.util.HashMap
java -agentlib:native-image-agent=config-output-dir=config ReflectionDemo java.util.ArrayList
```


