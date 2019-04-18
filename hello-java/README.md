This is a very simple Java Hello World application that will be compiled into native executable. 

### Install GraalVM

We are using sdk to install the GraalVM. First check which version is available. 

```
sdk list java
```

Than install the latest version

```
sdk use java 1.0.0-rc-15-grl
```

### Compile to jar

There is a simple HelloWorld.java and we need to compile it into a jar first and run it with java command.

```
steve@freedom:~/networknt/light-example-graal/hello-java$ javac HelloWorld.java
steve@freedom:~/networknt/light-example-graal/hello-java$ java HelloWorld
Hello World!
```

### Compile to native

```
native-image HelloWorld
```

The output should be something like this.

```
steve@freedom:~/networknt/light-example-graal/hello-java$ native-image HelloWorld
Build on Server(pid: 17219, port: 44475)
[helloworld:17219]    classlist:     133.72 ms
[helloworld:17219]        (cap):     729.71 ms
[helloworld:17219]        setup:     935.94 ms
[helloworld:17219]   (typeflow):   1,502.31 ms
[helloworld:17219]    (objects):     323.34 ms
[helloworld:17219]   (features):      61.09 ms
[helloworld:17219]     analysis:   1,918.61 ms
[helloworld:17219]     universe:      76.27 ms
[helloworld:17219]      (parse):     105.93 ms
[helloworld:17219]     (inline):     489.77 ms
[helloworld:17219]    (compile):     601.70 ms
[helloworld:17219]      compile:   1,314.41 ms
[helloworld:17219]        image:     133.42 ms
[helloworld:17219]        write:      41.03 ms
[helloworld:17219]      [total]:   4,585.56 ms

```

A  helloworld native application should be found in the same folder. 

```
-rwxr-xr-x 1 steve steve 2541504 Apr 17 23:44 helloworld
```

### Run it

```
steve@freedom:~/networknt/light-example-graal/hello-java$ ./helloworld 
Hello World!
```

