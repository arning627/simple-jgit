package cn.arning.jgit;

import cn.arning.gittools.conf.GitAuthentication;
import cn.arning.gittools.utils.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class JgitApplicationTests {


    String path = "/Users/arning/Desktop/tmp/gitTest/.git";

    @Test
    void contextLoads() {
        File file = new File("/Users/arning/develop/code/my");
        List<File> localGitRepository = FileUtil.findLocalGitRepository(file, new ArrayList<File>());
        for (File file1 : localGitRepository) {
            String absolutePath = file1.getAbsolutePath();
            System.out.println(absolutePath);
        }

        String property = System.getProperty("java.home");
        System.out.println(property);
    }

    @Test
    void test1() throws IOException, GitAPIException {
        File file = new File(path);
        Git git = Git.open(file);
        String branch = git.getRepository().getBranch();
        System.out.println(branch);
    }

    @Test
    void test2() throws Exception {
        File file = new File(path);
        Git open = Git.open(file);
        List<DiffEntry> call = open.diff().call();
        for (DiffEntry diffEntry : call) {
            String newPath = diffEntry.getNewPath();
            if (newPath.startsWith(".")) {
                continue;
            }
            DirCache add = open.add().addFilepattern(newPath).call();
        }
        open.commit().setMessage("测试提交").call();
        open.push().setRemote("origin").setCredentialsProvider(GitAuthentication.authentication()).call();
    }

    @Test
    void testStash() throws IOException, GitAPIException {
        File file = new File(path);
        Git git = Git.open(file);
        git.stashCreate().setWorkingDirectoryMessage("").call();

        Collection<RevCommit> call = git.stashList().call();
        for (RevCommit revCommit : call) {
            String s = revCommit.toString();
            System.out.println(s);
        }
    }

    @Test
    void deleteTag() throws IOException, GitAPIException {
        File file = new File(path);
        Git git = Git.open(file);
        List<String> call = git.tagDelete().setTags("1.1.1").call();

        git.push().setPushTags().setCredentialsProvider(GitAuthentication.authentication()).call();
    }

    @Test
    void cloneTest() throws GitAPIException {
        List<String> cloneUrl = FileUtil.findCloneUrl(new File("/Users/arning/cloneUrl.txt"));
        for (int i = 0; i < 5; i++) {
            String s = cloneUrl.get(i);
            File file = new File("/Users/arning/Desktop/tmp/cloneTes" + s.substring(s.lastIndexOf("/")));
            if (!file.exists()) {
                file.mkdir();
                System.out.println("创建完成" + file.getName());
                Git git = Git.cloneRepository().setURI(s).setDirectory(file).setCredentialsProvider(null).call();
            }
        }
    }

    @Test
    void branchTest() throws IOException, GitAPIException {
        File file = new File("/Users/arning/develop/devops/tagDir/eb-flyway-db-full/.git");
        Git open = Git.open(file);
        List<Ref> local = open.branchList().call();
        List<Ref> remote = open.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
        for (Ref ref : local) {
            System.out.println(ref.getName());
        }
        System.out.println("=============");
        for (Ref ref : remote) {
            System.out.println(ref.getName());
        }

    }

    @Test
    void easyexcelTest() {
        File file = new File("/Users/arning/Desktop/tmp/easytest.xlsx");
        String name = file.getName();
        ExcelReader excelReader = null;
        try {
//            excelReader = EasyExcel.read(name, ExcelData.class, new ExcelDataListener()).build();
            ReadSheet build = EasyExcel.readSheet(0).build();
            excelReader.read(build);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != excelReader) {
                excelReader.finish();
            }
        }
    }


}
