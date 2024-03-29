package cn.arning.gittools.command.impl;

import cn.arning.gittools.command.Execute;
import cn.arning.gittools.conf.GitAuthentication;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;


/**
 * @author arning
 */
public class PushCode implements Execute {


    @Override
    public void execute(Git git, String describe, String version) {
        try {
            List<DiffEntry> call = git.diff().call();
            for (DiffEntry diffEntry : call) {
                String diffEntryNewPath = diffEntry.getNewPath();
                if (diffEntryNewPath.startsWith(".")) {
                    continue;
                }
                git.add().addFilepattern(diffEntryNewPath).call();
            }
            git.commit().setMessage(describe).call();
            git.push().setRemote("origin").setCredentialsProvider(GitAuthentication.authentication()).call();
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}
