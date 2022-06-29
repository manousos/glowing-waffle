# Glowing waffle

Collection of custom rules to be appended to the [Maven Enforcer plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/).

## Description

Rules (actually the `enforce` Maven goal) are executed during the `validate` Maven phase, the same of the Maven Enforcer plugin.

## Getting started

Append this project dependency to the Maven Enforcer plugin, then add any rule at will, like `requireReleaseDepsInPlugins`. The result looks as following:

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

In order to only enforce the declared rules, issue the command:
```sh
mvn validate
```

Most-common phases like `package` and `test` in the `default` lifecycle include the `validate` phase. This means that Maven enforces the rules in addition to the usual build steps.

## Rules

The project currently supports one rule only.

### requireReleaseDepsInPlugins

> Ban SNAPSHOT dependencies of plugins.

In other words, if any plugin in a pom declares a SNAPSHOT dependency, the rule logs it and makes the build to fail.

Plugins in children modules are scanned too.

This rule does not currently accept any parameter.

#### Details

The rule doesn't do shortcircuitry. In other words, it doesn't stop at the first violation, but it finds all violations and log them before failing.

The rule is not cached.

### requireModuleNamingConvention

> Enforce that modules' names match a convention (regex).

In other words, if the pom declares a module whose name doesn't match a regex, the rule logs it and makes the build to fail.

This rule accepts the parameter `regex`, whose value is a regular expression. For instance:

```xml
<rules>
    <requireModuleNamingConvention implementation="org.apache.maven.enforcer.rule.requireModuleNamingConvention">
        <regex>.+-service</regex> <!-- grant any module that has at least one character before terminating with '-service' -->
    </requireModuleNamingConvention>
</rules>
```

#### Details

The rule doesn't do shortcircuitry. In other words, it doesn't stop at the first violation, but it finds all violations and log them before failing.

The rule is not cached.

## Bug report and contributions

If you think any rule has bugs or should be configurable or for any other issue/suggestion, please open a [GitHub issue for this project](https://github.com/Thefolle/requireReleaseDepsInPlugins/issues).

## License

This project is licensed under the terms of the [MIT License](http://www.opensource.org/licenses/mit-license.php).