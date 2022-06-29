package org.apache.maven.enforcer.rule;

import java.util.regex.PatternSyntaxException;

import org.apache.maven.artifact.ArtifactUtils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * @author <a href="mailto:cdavide8@gmail.com">Davide Calarco</a>
 */
public class requireModuleNamingConvention
        implements EnforcerRule {

    private String regex;

    public void execute(EnforcerRuleHelper helper)
            throws EnforcerRuleException {

        //Log log = helper.getLog();

        MavenProject project;

        try {
            project = (MavenProject) helper.evaluate("${project}");
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
        }

        
        requireModuleNamingConventionRule(project);
    }

    /**
     * This method could be merged into the execute method, but this last one is not testable because its parameter EnforcerRuleHelper cannot be directly instantiated.
     * @param project The project to check the rule against.
     * @throws EnforcerRuleException Signals the Maven Enforcer plugin to fail the build.
     */
    public void requireModuleNamingConventionRule(MavenProject project) throws EnforcerRuleException {
        boolean shouldFail = false;

        StringBuffer message = new StringBuffer();

        shouldFail = project.getModules()
            .stream()
            .map(moduleName -> {
                try {
                    if (!moduleName.matches(regex)) {
                        message.append("The name of the module " + moduleName + " is not compliant with the convention " + regex + "\n");
                        return true;
                    }
                    return false;
                } catch (PatternSyntaxException exception) {
                    exception.printStackTrace();
                    return false;
                }
                
            })
            .reduce(false, (accumulator, isNotCompliant) -> accumulator || isNotCompliant);

        if (shouldFail) {
            throw new EnforcerRuleException(message.toString());
        }
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

    /**
     * Injects the value of the regex parameter into the custom rule.
     * 
     * @param regex The regex each module name has to match against.
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

}
