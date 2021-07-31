# Ktlint Maven Plugin
[![Build Status](https://travis-ci.com/z3d1k/ktlint-maven-plugin.svg?branch=master)](https://travis-ci.com/z3d1k/ktlint-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.z3d1k/ktlint-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.z3d1k%22%20a%3A%22ktlint-maven-plugin%22)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Plugin for running [ktlint](https://github.com/pinterest/ktlint) in maven projects.

## Usage
### Check code style
Just add this code inside ```<build><plugins>...</plugins></build>``` section of _pom.xml_ in your project:
```xml
...
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <executions>
        <execution>
            <goals>
                <goal>lint</goal>
            </goals>
        </execution>
    </executions>
</plugin>
...
```
By default, it would run code style check against [standart ruleset](https://github.com/pinterest/ktlint#standard-rules) before code compilation takes place - on `validate` phase (see [Maven Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html) for more). Plugin will report violations in maven build log.

If you want to run check after code compilation - you can configure it to run on `verify` phase like this:
```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <executions>
        <execution>
            <id>ktlint-lint</id>
            <phase>verify</phase>
            <goals>
                <goal>lint</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
You also can run it manually by executing `mvn ktlint:lint`.

### Format sources
To format kotlin source files in your project you could add format goal to plugin configuration:
```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <executions>
        <execution>
            <id>ktlint-format</id>
            <phase>validate</phase>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
to run it automatically on every build, or run it manually `mvn ktlint:format`.

### Using baseline
Ktlint also provides baseline functionality to provide ability to run checks on new code, ignoring existing style violations. To generate baseline file add it's desired path to configuration
```xml
...
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <configuration>
        <baseline>${project.basedir}/baseline.xml</baseline>
    </configuration>
</plugin>
...
```
and run `mvn ktlint:generate-baseline`. After this `lint` goal would use generated file to ignore known violations.

## Configuration
### Example configuration:
```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <configuration>
         <includes>src/**/*.kt</includes>
         <excludes>src/**/Generated*.kt</excludes>
         <enableExperimentalRules>true</enableExperimentalRules>
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
</plugin>
```
Some options also could be configured through [EditorConfig file](https://github.com/pinterest/ktlint#editorconfig).

### Goals parameters:

| Parameter  | Default value | Goals            | Description                                               |
| ---------- | ------------- | ---------------- | --------------------------------------------------------- |
| `baseline` | - | `lint`, `format`, `generate-baseline` | Path to baseline file |
| `includes` | `src/**/*.kt` | `lint`, `format` | Use only files, location of which match specified pattern |
| `excludes` | - | `lint`, `format` | Ignore files, location of which match specified pattern |
| `enableExperimentalRules` | `false` | `lint`, `format` | Enable [experimental ruleset](https://github.com/pinterest/ktlint#experimental-rules) |
| `failOnError` | `true` | `lint` | Fail build if any violation found during execution |
| `reporters` | - | `lint` | Configuration of additional reporters, see [reporters configuration](#Reporters-configuration) |

### Reporters configuration

To enable additional reporters you need to add it's configuration to the ```<configuration><reporters>...</reporters></configuration>```.
Parameters should be specified if following format:
```xml
<{reporter name}.{parameter name}>{value}</{reporter name}.{parameter name}>
```
e.g.
```xml
<checkstyle.output>${project.build.directory}/ktlint.xml</checkstyle.output>
```

> Every reporter should have `output` parameter.

By default, following reporters are available: `checkstyle`, `json`, `html` and `plain`. For more information see [ktlint documentation](https://github.com/pinterest/ktlint)

### Using 3rd party rulesets or reporters
To use rulesets or reporters not included in ktlint by default you should add them to plugin dependencies:
```xml
<plugin>
    <groupId>com.github.z3d1k</groupId>
    <artifactId>ktlint-maven-plugin</artifactId>
    <version>0.5.3</version>
    <executions>
        ...
    </executions>
    <configuration>
        ...
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>awesome-ktlint-ruleset</artifactId>
            <version>0.1.0</version>
        </dependency>
    </dependencies>
</plugin>
```

## Legal
This project is not affiliated with nor endorsed by JetBrains or Pinterest.

All code, unless specified otherwise, is licensed under the [MIT](https://opensource.org/licenses/MIT) license.
