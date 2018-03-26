# Ktlint Maven Plugin
[![Build Status](https://travis-ci.org/z3d1k/ktlint-maven-plugin.svg?branch=master)](https://travis-ci.org/z3d1k/ktlint-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.z3d1k/ktlint-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.z3d1k%22%20a%3A%22ktlint-maven-plugin%22)

Wrapper plugin over the [ktlint](https://github.com/shyiko/ktlint) project for maven

## Usage
```
<build>
    <plugins>
        ...
        <plugin>
            <groupId>com.github.z3d1k</groupId>
            <artifactId>ktlint-maven-plugin</artifactId>
            <version>0.1.2</version>
            <executions>
                <execution>
                    <goals>
                        <goal>lint</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```
### Configuration
```
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.1.2</version>
    <executions>
        <goals>
            <goal>lint</goal>
        </goals>
        <configuration>
             <includes>src/**/*.kt</includes>
             <excludes>src/**/Generated*.kt</excludes>
             <outputToConsole>true</outputToConsole>
             <color>true</color>
             <groupByFile>true</groupByFile>
             <pad>true</pad>
             <verbose>true</verbose>
             <checkstyleReportPath>${project.build.directory}/ktlint.xml</checkstyleReportPath>
             <jsonReportPath>${project.build.directory}/ktlint.json</jsonReportPath>
             <failOnError>true</failOnError>
         </configuration>
    </executions>
</plugin>
```
