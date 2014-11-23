#jasmine-gwt

Simple GWT wrapper for jasmine testing framework

Look inside the example module...

[![Build Status](https://drone.io/github.com/gwt-noo/jasmine-wrapper/status.png)](https://drone.io/github.com/gwt-noo/jasmine-wrapper/latest)

To run the example:
```Shell
gradlew :example:gwtSuperDev
```

Then navigate to: 
```
http://localhost:9876/jasmine.example.JasmineExample/jasmine/runner.html
```

# Building and deploying

Just run
```Shell
gradlew :jasmine-gwt-wrapper:bintrayUpload
```

# Maven / Gradle

The repository is located at
```
http://dl.bintray.com/gwt-noo/maven
```

Only version 0.1.3 and on will be available there.

Artifact
```
<dependency>
        <groupId>com.github.gwt-noo</groupId>
        <artifactId>jasmine-gwt-wrapper</artifactId>
        <version>VERSION</version>
        <type>jar</type>
</dependency>
```
or
```
compile(group: 'com.github.gwt-noo', name: 'jasmine-gwt-wrapper', version: 'VERSION')
```

# Change log

## 0.1.6
 - Once again, recompilation happens when the test page is refreshed
 - If no recompilation, the runner will load the normal .nocache module file

## 0.1.5
 - Added compile button in runner.html
 - GWT 2.7.0 is now in place

## 0.1.4
 - Added support for compiling the test. In the runner.html,
    if no .recompile file is loaded, the normal .nocache will be loaded

## 0.1.3
 - Changing namespace
 - Hosting of binaries moved

## 0.1.2
 - Upgrade to GWT 2.7.0-rc1
 - Auto compile on refresh of the jasmine runner page

## 0.1.1
 - Initial version

# License

This library include the jasmine library, which is distributed with the MIT license.
So some Apache2 and MIT licensing apply.