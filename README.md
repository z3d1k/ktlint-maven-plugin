# Ktlint Maven Plugin
[![Build Status](https://travis-ci.org/z3d1k/ktlint-maven-plugin.svg?branch=master)](https://travis-ci.org/z3d1k/ktlint-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.z3d1k/ktlint-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.z3d1k%22%20a%3A%22ktlint-maven-plugin%22)
[ ![Download](https://api.bintray.com/packages/z3d1k/maven/ktlint-maven-plugin/images/download.svg) ](https://bintray.com/z3d1k/maven/ktlint-maven-plugin/_latestVersion)

Wrapper plugin over the [ktlint](https://github.com/shyiko/ktlint) project for maven

## Usage
```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>com.github.z3d1k</groupId>
            <artifactId>ktlint-maven-plugin</artifactId>
            <version>0.1.3</version>
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
```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.1.3</version>
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
             <verbose>true</verbose>
             <reporters>
                 <checkstyle.output>${project.build.directory}/ktlint.xml</checkstyle.output>
                 <json.output>${project.build.directory}/ktlint.json</json.output>
                 <plain.output>${project.build.directory}/ktlint.txt</plain.output>
                 <plain.color>true</plain.color>
                 <plain.pad>false</plain.pad>
                 <plain.group_by_file>true</plain.group_by_file>
             </reporters>
             <failOnError>true</failOnError>
         </configuration>
    </executions>
</plugin>
```
#### Custom rules
To use any custom (3rd party) ktlint rules just add artifact to plugin dependencies

#### Custom reporters
To use any custom (3rd party) ktlint reporter just add artifact to plugin dependencies and to reporters configuration.

```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.1.3</version>
    <executions>
        ...
        <configuration>
             ...
             <reporters>
                 ...
                 <name.output>outputPath</name.output>
                 <name.prop1>value1</name.prop1>
                 <name.prop2>value2</name.prop2>
                 ...
             </reporters>
             ...
         </configuration>
    </executions>
    <dependencies>
        <dependency>
            <groupId>groupId</groupId>
            <artifactId>artifactId</artifactId>
            <version>version</version>
        </dependency>
    </dependencies>
</plugin>

```
*Note: every reporter in ___reporters___ configuration section require ___output___ property.
