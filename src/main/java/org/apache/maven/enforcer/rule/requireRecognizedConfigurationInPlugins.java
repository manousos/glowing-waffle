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
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class requireRecognizedConfigurationInPlugins implements EnforcerRule2, Contextualizable {

    private EnforcerLevel level = EnforcerLevel.ERROR;

    /**
     * Component used to get a plugin descriptor from a given plugin.
     */
    private MavenPluginManagerHelper pluginManager;

    public void execute(EnforcerRuleHelper helper)
            throws EnforcerRuleException {

        // Log log = helper.getLog();

        MavenProject project;
        MavenSession session;

        try {
            project = (MavenProject) helper.evaluate("${project}");
            session = (MavenSession) helper.evaluate("${session}");
            pluginManager = (MavenPluginManagerHelper) helper.getComponent(MavenPluginManagerHelper.class);
        } catch (ExpressionEvaluationException | ComponentLookupException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }

        requireRecognizedConfigurationInPluginsRule(project, session);
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
    public void requireRecognizedConfigurationInPluginsRule(MavenProject project, MavenSession session)
            throws EnforcerRuleException {
        boolean shouldFail = false;

        StringBuffer message = new StringBuffer();

        /*
         * The original model doesn't still have plugins declared in pluginManagement,
         * quite just those inside build/plugins
         * Project and execution projects do not make this distinction anymore
         */
        // System.out.println(project.getBuild().getPlugins().size());
        // System.out.println(project.getExecutionProject().getBuild().getPlugins().size());
        // System.out.println(project.getOriginalModel().getBuild().getPlugins().size());

        project.getOriginalModel().getBuild().getPlugins()
                .forEach(plugin -> {
                    try {
                        List<String> parameters = getPluginParameters(session, plugin);

                        Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
                        enforceRule(plugin, parameters, configuration)
                                .forEach(nonRecognizedParameter -> {
                                    message.append(
                                            "The plugin " + plugin.getKey()
                                                    + " does not accept a parameter called "
                                                    + nonRecognizedParameter + ". Please, correct or remove it.");
                                });

                        plugin.getExecutions().stream()
                                .map(execution -> (Xpp3Dom) execution.getConfiguration())
                                .forEach(executionConfiguration -> {
                                    enforceRule(plugin, parameters, executionConfiguration)
                                            .forEach(nonRecognizedParameter -> {
                                                message.append(
                                                        "The plugin " + plugin.getKey()
                                                                + " does not accept a parameter called "
                                                                + nonRecognizedParameter
                                                                + ". Please, correct or remove it.");
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

    private List<String> getPluginParameters(MavenSession session, Plugin plugin) throws Exception {
        PluginDescriptor pluginDescriptor;
        List<String> parameters = new ArrayList<>();
        pluginDescriptor = pluginManager.getPluginDescriptor(plugin, session);

        pluginDescriptor.getMojos().stream()
                .forEach(mojoDescriptor -> {
                    parameters.addAll(mojoDescriptor.getParameters().stream()
                            .map(parameter -> parameter.getName()).collect(Collectors.toList()));
                });

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

    @Override
    public void contextualize(Context context) throws ContextException {
        System.out.println("ciao");
        System.out.println(context.getContextData());
    }

    public void setLevel(String level) {
        this.level = EnforcerLevel.valueOf(level.toUpperCase()); // should throw exception if the string is not
                                                                 // recognized
    }

    @Override
    public EnforcerLevel getLevel() {
        return level;
    }

}
