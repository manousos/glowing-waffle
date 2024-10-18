package org.apache.maven.enforcer.rule;

import java.util.stream.Collectors;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * @author <a href="mailto:manoumathioudakis@yahoo.gr">Manousos Mathioudakis</a>
 */
public class requireReleaseDepsInDependencies
    implements EnforcerRule {

  public void execute(EnforcerRuleHelper helper)
      throws EnforcerRuleException {

    MavenProject project;

    try {
      project = (MavenProject) helper.evaluate("${project}");
    } catch (ExpressionEvaluationException e) {
      throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(),
          e);
    }

    requireReleaseDepsInDependenciesRule(project);
  }

  /**
   * This method could be merged into the execute method, but this last one is not testable because
   * its parameter EnforcerRuleHelper cannot be directly instantiated.
   *
   * @param project The project to check the rule against.
   * @throws EnforcerRuleException Signals the Maven Enforcer plugin to fail the build.
   */
  public void requireReleaseDepsInDependenciesRule(MavenProject project)
      throws EnforcerRuleException {

    var errors = project.getDependencies()
        .stream()
        .filter(dependency -> ArtifactUtils.isSnapshot(dependency.getVersion()))
        .map(dep -> String.format("Found SNAPSHOT dependency in %s:%s:%s",
            dep.getGroupId(), dep.getArtifactId(), dep.getVersion()))
        .collect(Collectors.joining("\n"));

    if (!errors.isEmpty()) {
      throw new EnforcerRuleException(errors);
    }
  }

  /**
   * If your rule is cacheable, you must return a unique id when parameters or conditions change
   * that would cause the result to be different. Multiple cached results are stored based on their
   * id.
   * <p>
   * The easiest way to do this is to return a hash computed from the values of your parameters.
   * <p>
   * If your rule is not cacheable, then the result here is not important, you may return anything.
   */
  public String getCacheId() {
    // no hash on boolean...only parameter so no hash is needed.
    // return Boolean.toString(this.shouldIfail);
    return "";
  }

  /**
   * This tells the system if the results are cacheable at all. Keep in mind that during forked
   * builds and other things, a given rule may be executed more than once for the same project. This
   * means that even things that change from project to project may still be cacheable in certain
   * instances.
   */
  public boolean isCacheable() {
    return false;
  }

  /**
   * If the rule is cacheable and the same id is found in the cache, the stored results are passed
   * to this method to allow double checking of the results. Most of the time this can be done by
   * generating unique ids, but sometimes the results of objects returned by the helper need to be
   * queried. You may for example, store certain objects in your rule and then query them later.
   */
  public boolean isResultValid(EnforcerRule rule) {
    return false;
  }

}
