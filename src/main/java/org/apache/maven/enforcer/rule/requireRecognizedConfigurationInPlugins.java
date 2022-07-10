package org.apache.maven.enforcer.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRule2;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.exec.MavenPluginManagerHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class requireRecognizedConfigurationInPlugins implements EnforcerRule2 {

    private EnforcerLevel level = EnforcerLevel.ERROR;

    public void execute(EnforcerRuleHelper helper)
            throws EnforcerRuleException {

        // Log log = helper.getLog();

        MavenProject project;
        MavenSession session;
        MavenPluginManagerHelper mavenPluginManagerHelper;

        try {
            project = (MavenProject) helper.evaluate("${project}");
            session = (MavenSession) helper.evaluate("${session}");
            mavenPluginManagerHelper = (MavenPluginManagerHelper) helper.getComponent(MavenPluginManagerHelper.class);
            requireRecognizedConfigurationInPluginsRule(project, session, mavenPluginManagerHelper);
        } catch (ExpressionEvaluationException | ComponentLookupException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }

    }

    /**
     * This method could be merged into the execute method, but this last one is not
     * testable because its parameter EnforcerRuleHelper cannot be directly
     * instantiated.
     * 
     * @param project The project to check the rule against.
     * @throws EnforcerRuleException Signals the Maven Enforcer plugin to fail the
     *                               build.
     */
    public void requireRecognizedConfigurationInPluginsRule(MavenProject project, MavenSession session, MavenPluginManagerHelper mavenPluginManagerHelper)
            throws EnforcerRuleException {
        boolean shouldFail = false;

        StringBuffer message = new StringBuffer();

        /*
         * The original model has not applied inheritance yet, unlike project and
         * executionProject.
         */
        // System.out.println(project.getBuild().getPlugins().size());
        // System.out.println(project.getExecutionProject().getBuild().getPlugins().size());
        // System.out.println(project.getOriginalModel().getBuild().getPlugins().size());

        List<Plugin> plugins = new ArrayList<>();
        plugins.addAll(project.getBuildPlugins());
        // plugins.addAll(project.getPluginManagement().getPlugins());

        plugins
                .forEach(plugin -> {
                    try {
                        List<String> parameters = getPluginParameters(session, plugin, mavenPluginManagerHelper);
                        List<Xpp3Dom> configurations = new ArrayList<>();
                        configurations.add((Xpp3Dom) plugin.getConfiguration());
                        configurations.addAll(plugin.getExecutions().stream()
                                .map(execution -> (Xpp3Dom) execution.getConfiguration()).collect(Collectors.toList()));

                        configurations
                                .forEach(configuration -> {
                                    enforceRule(plugin, parameters, configuration)
                                            .forEach(nonRecognizedParameter -> {
                                                message.append(
                                                        "The plugin " + plugin.getKey()
                                                                + " does not accept a parameter called "
                                                                + nonRecognizedParameter
                                                                + ". Please, correct or remove it.\n");
                                            });
                                });

                    } catch (Exception e) {
                        message.append(e.toString());
                    }

                });

        shouldFail = message.length() > 0;

        if (shouldFail) {
            throw new EnforcerRuleException(message.toString());
        }
    }

    private List<String> enforceRule(Plugin plugin, List<String> parameters, Xpp3Dom configuration) {
        List<String> nonRecognizedParameters = new ArrayList<>();
        if (configuration != null) {
            List<String> actualParameters = Arrays.asList(configuration.getChildren()).stream()
                    .map(node -> node.getName()).collect(Collectors.toList());
            actualParameters
                    .stream()
                    .forEach(actualParameter -> {
                        if (!parameters.contains(actualParameter)) {
                            nonRecognizedParameters.add(actualParameter);
                        }
                    });
        }
        return nonRecognizedParameters;
    }

    private List<String> getPluginParameters(MavenSession session, Plugin plugin, MavenPluginManagerHelper mavenPluginManagerHelper) throws Exception {
        PluginDescriptor pluginDescriptor;
        List<String> parameters = new ArrayList<>();
        pluginDescriptor = mavenPluginManagerHelper.getPluginDescriptor(plugin, session);
        pluginDescriptor.getMojos().stream()
                .forEach(mojoDescriptor -> {
                    parameters.addAll(mojoDescriptor.getParameters().stream()
                            .map(parameter -> parameter.getName()).collect(Collectors.toList()));
                });
        
        /***
         * For the reason about this unique use case, see:
         *  https://maven.apache.org/plugins-archives/maven-site-plugin-3.7.1/maven-3.html#Classic_configuration_.28Maven_2_.26_3.29
         *  https://maven.apache.org/shared/maven-reporting-exec/
         */
        if (plugin.getKey().contentEquals("org.apache.maven.plugins:maven-site-plugin")) {
            parameters.add("reportPlugins");
        }

        return parameters;
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or
     * conditions
     * change that would cause the result to be different. Multiple cached results
     * are stored
     * based on their id.
     * 
     * The easiest way to do this is to return a hash computed from the values of
     * your parameters.
     * 
     * If your rule is not cacheable, then the result here is not important, you may
     * return anything.
     */
    public String getCacheId() {
        // no hash on boolean...only parameter so no hash is needed.
        // return Boolean.toString(this.shouldIfail);
        return "";
    }

    /**
     * This tells the system if the results are cacheable at all. Keep in mind that
     * during
     * forked builds and other things, a given rule may be executed more than once
     * for the same
     * project. This means that even things that change from project to project may
     * still
     * be cacheable in certain instances.
     */
    public boolean isCacheable() {
        return false;
    }

    /**
     * If the rule is cacheable and the same id is found in the cache, the stored
     * results
     * are passed to this method to allow double checking of the results. Most of
     * the time
     * this can be done by generating unique ids, but sometimes the results of
     * objects returned
     * by the helper need to be queried. You may for example, store certain objects
     * in your rule
     * and then query them later.
     */
    public boolean isResultValid(EnforcerRule rule) {
        return false;
    }

    // This method works for plugins
    // @Override
    // public void contextualize(Context context) throws ContextException {
    //     System.out.println("ciao");
    //     System.out.println(context.getContextData());
    // }

    public void setLevel(String level) {
        this.level = EnforcerLevel.valueOf(level.toUpperCase()); // should throw exception if the string is not
                                                                 // recognized
    }

    @Override
    public EnforcerLevel getLevel() {
        return level;
    }

}
