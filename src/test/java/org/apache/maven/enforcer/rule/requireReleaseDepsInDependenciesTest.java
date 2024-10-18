package org.apache.maven.enforcer.rule;

import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.ReaderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class requireReleaseDepsInDependenciesTest {

  private requireReleaseDepsInDependencies testObj;

  @BeforeEach
  void setUp() {
    testObj = new requireReleaseDepsInDependencies();
  }

  @Test
  void noSnaphots() throws Exception {
    File pom = getTestFile(
        "src/test/resources/unit/basic-test/pom-one-plugin-no-snapshot-dependencies.xml");
    MavenXpp3Reader pomReader = new MavenXpp3Reader();
    Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

    MavenProject testProject = new MavenProject(model);

    assertDoesNotThrow(() -> testObj.requireReleaseDepsInDependenciesRule(testProject));
  }

  @Test
  void hasSnaphots() throws Exception {
    File pom = getTestFile(
        "src/test/resources/unit/basic-test/pom-one-plugin-3-snapshot-dependencies.xml");
    MavenXpp3Reader pomReader = new MavenXpp3Reader();
    Model model = pomReader.read(ReaderFactory.newXmlReader(pom));

    MavenProject testProject = new MavenProject(model);

    assertThrows(EnforcerRuleException.class,
        () -> testObj.requireReleaseDepsInDependenciesRule(testProject));
  }
}
