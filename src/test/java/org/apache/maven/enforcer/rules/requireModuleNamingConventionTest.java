package org.apache.maven.enforcer.rules;

import java.io.File;

import org.apache.maven.enforcer.rule.requireModuleNamingConvention;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.ReaderFactory;

public class requireModuleNamingConventionTest extends AbstractMojoTestCase {
    
    protected void setUp() throws Exception {
        // do not call! Here testing an EnforcerRule class, not an AbstractMojo class!
        // super.setUp();
    }

    protected void tearDown() throws Exception {
        // do not call! Here testing an EnforcerRule class, not an AbstractMojo class!
        // super.tearDown();
    }

    public void testWhenNoModulesShouldPass() throws Exception {
        File pom = getTestFile("src/test/resources/unit/requireModuleNamingConvention/pom-no-modules.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireModuleNamingConvention rule = new requireModuleNamingConvention();
        try {
            rule.requireModuleNamingConventionRule(testProject);
        } catch (EnforcerRuleException e) {
            fail("Rule enforced unexpectedly!");
        }
    }

    public void testWhenOneModuleCompliantShouldPass() throws Exception {
        File pom = getTestFile("src/test/resources/unit/requireModuleNamingConvention/pom-one-module-compliant.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireModuleNamingConvention rule = new requireModuleNamingConvention();

        /* Manually inject parameter */
        org.codehaus.plexus.util.xml.Xpp3Dom configuration = (org.codehaus.plexus.util.xml.Xpp3Dom) testProject.getBuildPlugins().get(0).getExecutions().get(0).getConfiguration();
        String regex = configuration.getChild("rules").getChild("requireModuleNamingConvention").getChild("regex").getValue();
        rule.setRegex(regex);

        try {
            rule.requireModuleNamingConventionRule(testProject);
        } catch (EnforcerRuleException e) {
            fail("Rule enforced unexpectedly!");
        }
    }

    public void testWhenOneModuleNonCompliantShouldThrowEnforcerRuleException() throws Exception {
        File pom = getTestFile("src/test/resources/unit/requireModuleNamingConvention/pom-one-module-non-compliant.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireModuleNamingConvention rule = new requireModuleNamingConvention();

        /* Manually inject parameter */
        org.codehaus.plexus.util.xml.Xpp3Dom configuration = (org.codehaus.plexus.util.xml.Xpp3Dom) testProject.getBuildPlugins().get(0).getExecutions().get(0).getConfiguration();
        String regex = configuration.getChild("rules").getChild("requireModuleNamingConvention").getChild("regex").getValue();
        rule.setRegex(regex);

        try {
            rule.requireModuleNamingConventionRule(testProject);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }

    public void testWhenMultipleModulesCompliantShouldPass() throws Exception {
        File pom = getTestFile("src/test/resources/unit/requireModuleNamingConvention/pom-multiple-modules-compliant.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireModuleNamingConvention rule = new requireModuleNamingConvention();

        /* Manually inject parameter */
        org.codehaus.plexus.util.xml.Xpp3Dom configuration = (org.codehaus.plexus.util.xml.Xpp3Dom) testProject.getBuildPlugins().get(0).getExecutions().get(0).getConfiguration();
        String regex = configuration.getChild("rules").getChild("requireModuleNamingConvention").getChild("regex").getValue();
        rule.setRegex(regex);

        try {
            rule.requireModuleNamingConventionRule(testProject);
        } catch (EnforcerRuleException e) {
            fail("Rule enforced unexpectedly!");
        }
    }

    public void testWhenMultipleModulesNonCompliantShouldThrowEnforcerRuleException() throws Exception {
        File pom = getTestFile("src/test/resources/unit/requireModuleNamingConvention/pom-multiple-modules-non-compliant.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

        MavenProject testProject = new MavenProject(model);

        requireModuleNamingConvention rule = new requireModuleNamingConvention();

        /* Manually inject parameter */
        org.codehaus.plexus.util.xml.Xpp3Dom configuration = (org.codehaus.plexus.util.xml.Xpp3Dom) testProject.getBuildPlugins().get(0).getExecutions().get(0).getConfiguration();
        String regex = configuration.getChild("rules").getChild("requireModuleNamingConvention").getChild("regex").getValue();
        rule.setRegex(regex);

        try {
            rule.requireModuleNamingConventionRule(testProject);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }

}
