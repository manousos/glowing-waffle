package org.apache.maven.enforcer.rules;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.enforcer.rule.requireRecognizedConfigurationInPlugins;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.InvalidPluginDescriptorException;
import org.apache.maven.plugin.PluginDescriptorParsingException;
import org.apache.maven.plugin.PluginResolutionException;
import org.apache.maven.plugin.descriptor.DuplicateMojoDescriptorException;
import org.apache.maven.plugin.descriptor.DuplicateParameterException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reporting.exec.MavenPluginManagerHelper;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Assertions;

public class requireRecognizedConfigurationInPluginsTest extends AbstractMojoTestCase {

    // @Override
    protected void setUp() throws Exception {
        setupContainer();
    }

    // @Override
    protected void tearDown() throws Exception {
        teardownContainer();
    }
    
    public void testWhenExistingParameterShouldPass() throws IOException, XmlPullParserException, ComponentLookupException, EnforcerRuleException, ProjectBuildingException, ClassNotFoundException, UnknownRepositoryLayoutException, PluginResolutionException, PluginDescriptorParsingException, InvalidPluginDescriptorException, DuplicateParameterException, DuplicateMojoDescriptorException {
        File pom = getTestFile("src/test/resources/unit/requireRecognizedConfigurationInPlugins/pom-existing-parameter.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));
        MavenProject testProject = new MavenProject(model);
        
        /***
         * This approach tries to download plugin descriptors in the flesh, but mocking them is way easier
         */
        // List<ArtifactRepository> artifactRepositories = new ArrayList<>();
        // ArtifactRepositoryFactory artifactRepositoryFactory = getContainer().lookup(ArtifactRepositoryFactory.class);
        // ArtifactRepositoryPolicy artifactRepositoryPolicy = new ArtifactRepositoryPolicy();
        // artifactRepositoryPolicy.setChecksumPolicy(ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE);
        // artifactRepositoryPolicy.setUpdatePolicy(ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS);
        // ArtifactRepository artifactRepository = artifactRepositoryFactory.createArtifactRepository("central", "https://repo.maven.apache.org/maven2", "default", artifactRepositoryPolicy, artifactRepositoryPolicy);
        // artifactRepositories.add(artifactRepository);
        // testProject.setPluginArtifactRepositories(artifactRepositories);

        requireRecognizedConfigurationInPlugins rule = new requireRecognizedConfigurationInPlugins();
        MavenSession mavenSession = newMavenSession(testProject);
        MavenPluginManagerHelper mavenPluginManagerHelper = mock(MavenPluginManagerHelper.class);

        // Create a mock plugin descriptor
        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        Parameter parameter = new Parameter();
        parameter.setName("fail");
        mojoDescriptor.addParameter(parameter);
        pluginDescriptor.addMojo(mojoDescriptor);
        Plugin plugin = testProject.getBuildPlugins().get(0);
        when(mavenPluginManagerHelper.getPluginDescriptor(plugin, mavenSession)).thenReturn(pluginDescriptor);

        try {
            rule.requireRecognizedConfigurationInPluginsRule(testProject, mavenSession, mavenPluginManagerHelper);
        } catch (EnforcerRuleException e) {
            Assertions.fail("Rule enforced unexpectedly!\n", e);
        }
    }

    public void testWhenNonExistingParameterShouldThrowEnforcerRuleException() throws IOException, XmlPullParserException, ComponentLookupException, EnforcerRuleException, ProjectBuildingException, ClassNotFoundException, UnknownRepositoryLayoutException, PluginResolutionException, PluginDescriptorParsingException, InvalidPluginDescriptorException, DuplicateParameterException, DuplicateMojoDescriptorException {
        File pom = getTestFile("src/test/resources/unit/requireRecognizedConfigurationInPlugins/pom-non-existing-parameter.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));
        MavenProject testProject = new MavenProject(model);

        requireRecognizedConfigurationInPlugins rule = new requireRecognizedConfigurationInPlugins();
        MavenSession mavenSession = newMavenSession(testProject);
        MavenPluginManagerHelper mavenPluginManagerHelper = mock(MavenPluginManagerHelper.class);

        // Create a mock plugin descriptor
        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        Parameter parameter = new Parameter();
        parameter.setName("fail");
        mojoDescriptor.addParameter(parameter);
        pluginDescriptor.addMojo(mojoDescriptor);
        Plugin plugin = testProject.getBuildPlugins().get(0);
        when(mavenPluginManagerHelper.getPluginDescriptor(plugin, mavenSession)).thenReturn(pluginDescriptor);

        try {
            rule.requireRecognizedConfigurationInPluginsRule(testProject, mavenSession, mavenPluginManagerHelper);
            fail("Rule not enforced!");
        } catch (EnforcerRuleException e) {
        }
    }

    public void testWhenExistingParametersAndMultipleExecutionsShouldPass() throws IOException, XmlPullParserException, ComponentLookupException, EnforcerRuleException, ProjectBuildingException, ClassNotFoundException, UnknownRepositoryLayoutException, PluginResolutionException, PluginDescriptorParsingException, InvalidPluginDescriptorException, DuplicateParameterException, DuplicateMojoDescriptorException {
        File pom = getTestFile("src/test/resources/unit/requireRecognizedConfigurationInPlugins/pom-existing-parameters-multiple-executions.xml");
        MavenXpp3Reader pomReader = new MavenXpp3Reader();
        Model model = pomReader.read(ReaderFactory.newXmlReader(pom));
        MavenProject testProject = new MavenProject(model);

        requireRecognizedConfigurationInPlugins rule = new requireRecognizedConfigurationInPlugins();
        MavenSession mavenSession = newMavenSession(testProject);
        MavenPluginManagerHelper mavenPluginManagerHelper = mock(MavenPluginManagerHelper.class);

        // Create a mock plugin descriptor
        PluginDescriptor pluginDescriptor = new PluginDescriptor();
        MojoDescriptor mojoDescriptor = new MojoDescriptor();
        Parameter parameter = new Parameter();
        parameter.setName("fail");
        mojoDescriptor.addParameter(parameter);
        pluginDescriptor.addMojo(mojoDescriptor);
        Plugin plugin = testProject.getBuildPlugins().get(0);
        when(mavenPluginManagerHelper.getPluginDescriptor(plugin, mavenSession)).thenReturn(pluginDescriptor);

        try {
            rule.requireRecognizedConfigurationInPluginsRule(testProject, mavenSession, mavenPluginManagerHelper);
        } catch (EnforcerRuleException e) {
            Assertions.fail("Rule enforced unexpectedly!\n", e);
        }
    }
    
}