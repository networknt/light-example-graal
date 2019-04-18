Follow the steps below to compile the main.kt into native application. 

### Compile into jar

The first step is to use kotlinc to compile the main.kt to main.jar

```
kotlinc main.kt -include-runtime -d main.jar
```

If you don't have kotlinc installed, use the following command to install it locally. 

```
sdk install kotlin
```

The size of the main.jar is about 1.27MB on my Ubuntu 18.04 desktop. 

```
-rw-r--r-- 1 steve steve 1265602 Apr 17 23:11 main.jar
```


### Run the App

Once you have the main.jar in the directory, you can run the following command to execute the app. 

```
java -jar main.jar
```

You should see the `Hello World!` output on the terminal. 


### Compile to native

Run the native-image command.

```
native-image --static -jar main.jar
```
The output should be something like

```
steve@freedom:~/networknt/light-example-graal/hello-kotlin$ native-image --static -jar main.jar
Build on Server(pid: 17219, port: 44475)
[main:17219]    classlist:     375.11 ms
[main:17219]        (cap):     747.34 ms
[main:17219]        setup:     965.10 ms
[main:17219]   (typeflow):   1,501.64 ms
[main:17219]    (objects):     357.08 ms
[main:17219]   (features):      58.20 ms
[main:17219]     analysis:   1,956.19 ms
[main:17219]     universe:      72.98 ms
[main:17219]      (parse):     104.27 ms
[main:17219]     (inline):     489.77 ms
[main:17219]    (compile):     802.20 ms
[main:17219]      compile:   1,546.20 ms
[main:17219]        image:     152.45 ms
[main:17219]        write:     140.06 ms
[main:17219]      [total]:   5,248.93 ms

```

Now you should have a main executable in the directory. 

```
-rwxr-xr-x 1 steve steve 3958816 Apr 17 23:21 main
```

### Run the native app

```
steve@freedom:~/networknt/light-example-graal/hello-kotlin$ ./main
Hello World!
```

