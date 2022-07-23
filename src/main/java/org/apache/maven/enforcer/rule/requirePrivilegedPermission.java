package org.apache.maven.enforcer.rule;

import java.io.File;
import java.io.FilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRule2;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;

public class requirePrivilegedPermission implements EnforcerRule2 {

    private EnforcerLevel level = EnforcerLevel.ERROR;

    @Override
    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
        // ThreadUtils.getAllThreads().stream().forEach(thread -> {
        //     helper.getLog().error(thread.getId() + ": " + thread.getPriority());
        // });

        /**
         *                  | Admin console | user console
         * is daemon            x               x
         * priority             5               5
         * missing perms        modifyThread, closeClassLoader, getClassLoader    setIO, modifyThread, closeClassLoader, getClassLoader
         */

        // AllPermission allPermission = new AllPermission();
        // helper.getLog().error(allPermission.getActions());

        // SecurityManager securityManager = new SecurityManager();
        
        // helper.getLog().error(Thread.currentThread().getPriority() + "");
        // System.setSecurityManager(securityManager);
        // RuntimePermission runtimePermission = new RuntimePermission("setIO");
        // helper.getLog().error(runtimePermission.getActions());
        // AccessController.checkPermission(new RuntimePermission(""));
        // AccessController.doPrivileged(new PrivilegedAction<Integer>() {
        //     @Override
        //     public Integer run() {
        //         return 3;
        //     }
        // });

        File file = new File("pom.xml");
        helper.getLog().error(file.exists() + "");
        helper.getLog().error("Write: " + file.canWrite() + "");
        helper.getLog().error("Read: " + file.canRead() + "");
        helper.getLog().error("Execute: " + file.canExecute() + "");
        // AccessController.checkPermission(new RuntimePermission("setIO"));
        // Thread[] threadArray = new Thread[20];
        // Thread.currentThread().getThreadGroup().enumerate(threadArray, true);
        // List<Thread> threads = List.of(threadArray);
        // threads.forEach(thread -> helper.getLog().error(thread.toString()));
        
        
        // AccessController.doPrivileged(new PrivilegedAction<Integer>() {
        //     @Override
        //     public Integer run() {
        //         // helper.getLog().error(new FilePermission("C:\\Users\\davide.calarco\\Desktop\\snippets\\Maven\\test-plugin\\demo\\pom.xml"));

        //         helper.getLog().error(file.canExecute() + "");
        //         return 0;
        //     } 
        // });
        // AccessController.checkPermission(new FilePermission("C:\\Users\\davide.calarco\\Desktop\\snippets\\Maven\\test-plugin\\demo\\pom.xml", "read"));
        
        // helper.getLog().error(System.getProperty("user.name"));
        // helper.getLog().error(System.getProperties().toString());
        // securityManager.checkPermission(new RuntimePermission("setIO"));
        // securityManager.checkPermission();

        // whoami -> regroup\davide.calarco
        
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    @Override
    public boolean isResultValid(EnforcerRule cachedRule) {
        return false;
    }

    @Override
    public String getCacheId() {
        return null;
    }

    @Override
    public EnforcerLevel getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = EnforcerLevel.valueOf(level.toUpperCase()); // should throw exception if the string is not
                                                                 // recognized
    }
    
}
