# Candidate rules for next iteration

- Check that all dependencies declared in the pom correspond to the latest in the remote repositories (https://stackoverflow.com/questions/10362540/how-to-check-the-lastest-dependency-on-maven);
- Fail the build when a directory contains an unwanted character (https://stackoverflow.com/questions/42341897/how-can-i-check-if-a-filename-in-my-maven-project-contains-certain-characters-a);
- Check a property no matters if it comes from environment or it is defined by the user (https://issues.apache.org/jira/projects/MENFORCER/issues/MENFORCER-399?filter=allopenissues)
- Specify parameters to rules from CLI: https://stackoverflow.com/questions/60265976/add-parameters-to-maven-enforcer-rules-called-from-the-command-line;
- Check elevated privileges: https://stackoverflow.com/questions/45423340/does-a-maven-plugin-exist-to-gracefully-check-elevated-privilege.