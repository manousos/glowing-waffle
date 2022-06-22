# requireReleaseDepsInPlugins

Rule appended to the [Maven Enforcer plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/) in order to ban SNAPSHOT dependencies of plugins.

## Description

If any plugin in a pom declares a SNAPSHOT dependency, the rule logs it and makes the build to fail.

Plugins in children modules are scanned too.

The rule (actually the `enforce` Maven goal) is executed during the `validate` Maven phase, the same of the Maven Enforcer plugin.

## Getting started

Append this project dependency to the Maven Enforcer plugin, then add the rule `requireReleaseDepsInPlugins`. The result looks as following:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <id>enforce-no-snapshots</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <!-- Add this rule (don't forget the 'implementation' hint! ) -->
                    <requireReleaseDepsInPlugins implementation="org.apache.maven.enforcer.rule.requireReleaseDepsInPlugins" />
                </rules>
                <fail>true</fail>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <!-- Add this dependency to the plugin -->
        <dependency>
            <groupId>it.thefolle</groupId>
            <artifactId>require-release-deps-in-plugins</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

In order to only enforce the rule, issue the command:
```sh
mvn validate
```

Most-common phases like `package` and `test` in the `default` lifecycle include the `validate` phase. This means that Maven enforces the rule in addition to the usual build steps.

## Configuration

This rule does not currently accept any parameter.

## Bug report and contributions

If you think the rule has bugs or should be configurable or for any other issue/suggestion, please open a [GitHub issue for this project](https://github.com/Thefolle/requireReleaseDepsInPlugins/issues).

## License

This project is licensed under the terms of the [MIT License](http://www.opensource.org/licenses/mit-license.php).