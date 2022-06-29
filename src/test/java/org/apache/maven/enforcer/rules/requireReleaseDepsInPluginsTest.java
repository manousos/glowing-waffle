package org.apache.maven.enforcer.rules;

import java.io.File;

import org.apache.maven.enforcer.rule.requireReleaseDepsInPlugins;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.ReaderFactory;

public class requireReleaseDepsInPluginsTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        // do not call! Here testing an EnforcerRule class, not an AbstractMojo class!
        // super.setUp();
    }

    protected void tearDown() throws Exception {
        // do not call! Here testing an EnforcerRule class, not an AbstractMojo class!
        // super.tearDown();
    }

    public void testWhenNoPluginsShouldPass() throws Exception {
        File pom = getTestFile("src/test/resources/unit/basic-test/pom-no-plugins.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireReleaseDepsInPlugins rule = new requireReleaseDepsInPlugins();
        try {
            rule.requireReleaseDepsInPluginsRule(testProject);
        } catch (EnforcerRuleException e) {
            fail("Rule enforced unexpectedly!");
        }
    }

    public void testWhenOnePluginNoSnapshotDependenciesShouldPass() throws Exception {
        File pom = getTestFile("src/test/resources/unit/basic-test/pom-one-plugin-no-snapshot-dependencies.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireReleaseDepsInPlugins rule = new requireReleaseDepsInPlugins();
        try {
            rule.requireReleaseDepsInPluginsRule(testProject);
        } catch (EnforcerRuleException e) {
            fail("Rule enforced unexpectedly!");
        }
    }

    public void testWhenOnePluginOneSnapshotDependencyShouldThrowEnforcerRuleException() throws Exception {
        // read the test pom
        File pom = getTestFile("src/test/resources/unit/basic-test/pom-one-plugin-one-snapshot-dependency.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireReleaseDepsInPlugins rule = new requireReleaseDepsInPlugins();
        try {
            rule.requireReleaseDepsInPluginsRule(testProject);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }

    public void testWhenOnePluginMultipleSnapshotDependenciesShouldThrowEnforcerRuleException() throws Exception {
        File pom = getTestFile("src/test/resources/unit/basic-test/pom-one-plugin-multiple-snapshot-dependencies.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireReleaseDepsInPlugins rule = new requireReleaseDepsInPlugins();
        try {
            rule.requireReleaseDepsInPluginsRule(testProject);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }

    public void testWhenMultiplePluginsOneSnapshotDependencyShouldThrowEnforcerRuleException() throws Exception {
        File pom = getTestFile("src/test/resources/unit/basic-test/pom-multiple-plugins-one-snapshot-dependency.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireReleaseDepsInPlugins rule = new requireReleaseDepsInPlugins();
        try {
            rule.requireReleaseDepsInPluginsRule(testProject);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }
}
