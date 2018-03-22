# Ktlint Maven Plugin
[![Build Status](https://travis-ci.org/z3d1k/ktlint-maven-plugin.svg?branch=master)](https://travis-ci.org/z3d1k/ktlint-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.z3d1k/ktlint-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.z3d1k/ktlint-maven-plugin)

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
