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

}
