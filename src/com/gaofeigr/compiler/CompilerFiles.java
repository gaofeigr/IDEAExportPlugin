package com.gaofeigr.compiler;

import com.gaofeigr.utils.ArrayUtils;
import com.gaofeigr.utils.FileUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.httpclient.util.DateUtil;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CompilerFiles {

    public CompilerFiles(DataContext file, String outPutPath) {
        this.file = file;
        this.outPutPath = outPutPath;
    }

    private DataContext file;

    private String outPutPath;

    //所有选中的java文件列表
    private List<String> javaFilePathList = new ArrayList<String>();

    //其他类型文件列表
    private List<String> otherFilePathList = new ArrayList<String>();

    //资源文件列表
    private List<String> resourceFilePathList = new ArrayList<String>();

    /**
     * 导出编译后的java文件
     */
    public void execExport() {
        try {
            //获得选中文件
            VirtualFile[] selectFiles = file.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
            //初始化文件列表
            initFileList(selectFiles);
            //编译并输出文件
            compilerFiles();
        } catch (Exception e) {
            System.err.println("编译失败！");
        } finally {
            javaFilePathList.clear();
            otherFilePathList.clear();
            resourceFilePathList.clear();
        }
    }

    /**
     * 编译文件
     */
    private void compilerFiles() throws Exception {
        String projectPath = getProjectPath();
        projectPath = projectPath.replaceAll("/", "\\\\");
        String outPutPath = this.outPutPath + "\\result\\result" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        String classFileOutPutPath = outPutPath + "\\WEB-INF\\classes";
        FileUtil.createFolder(outPutPath);
        if (ArrayUtils.isNotEmpty(javaFilePathList)) {
            FileUtil.createFolder(classFileOutPutPath);
            compiler(projectPath, classFileOutPutPath);
        }
        if (ArrayUtils.isNotEmpty(otherFilePathList)) {
            FileUtil.copyOtherFiles(otherFilePathList, projectPath+"src", classFileOutPutPath);
        }
        if (ArrayUtils.isNotEmpty(resourceFilePathList)) {
            FileUtil.copyResourceFiles(resourceFilePathList, projectPath+"WebContent", outPutPath);
        }
    }

    private void compiler(String projectPath, String outPutPath) throws IOException {
        List<String> options = new ArrayList<String>();
        options.add(buildClassPath(".", projectPath + "\\WebContent\\WEB-INF\\classes" , projectPath + "\\WebContent\\WEB-INF\\lib\\*","C:\\DevelopmentTools\\apache-tomcat-7.0.75\\lib\\*"));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaFileManager.Location oLocation = StandardLocation.CLASS_OUTPUT;
        fileManager.handleOption("-classpath", options.iterator());
        fileManager.handleOption("-encoding", Arrays.asList("UTF8").iterator());
        fileManager.setLocation(oLocation, Arrays.asList(new File[] { new File(outPutPath) }));
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(javaFilePathList);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
            if (task.call()) {
                System.out.println("操作成功！");
            } else {
                outErrorMessage(diagnostics);
                System.err.println("操作失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileManager != null) {
                fileManager.close();
            }
        }
    }

    /**
     * 构建引用的jar包目录
     * @param paths
     * @return
     */
    private static String buildClassPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (path.endsWith("*")) {
                path = path.substring(0, path.length() - 1);
                File pathFile = new File(path);
                for (File file : pathFile.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        sb.append(path);
                        sb.append(file.getName());
                        sb.append(System.getProperty("path.separator"));
                    }
                }
            } else {
                sb.append(path);
                sb.append(System.getProperty("path.separator"));
            }
        }
        return sb.toString();
    }

    /**
     * 输出编译错误信息
     * @param diagnostics
     * @throws Exception
     */
    private void outErrorMessage(DiagnosticCollector<JavaFileObject> diagnostics) throws Exception {
        List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
            diagnostic.getKind();
            if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
                throw new Exception(diagnostic.getMessage(null));
            }
        }
    }

    /**
     * 初始化文件列表
     * @param selectFiles 选中的文件
     */
    private void initFileList(VirtualFile[] selectFiles) {
        for (VirtualFile virtualFile : selectFiles) {
            //若有子文件(即选中的是目录)则遍历子文件
            if (virtualFile.getChildren() != null && virtualFile.getChildren().length != 0) {
                initFileList(virtualFile.getChildren());
            } else {
                String filePath = virtualFile.getCanonicalPath();
                //添加文件绝对路径
                if (filePath.contains("src")) {
                    if (filePath.endsWith(".java")) {
                        javaFilePathList.add(filePath);
                    } else {
                        otherFilePathList.add(filePath);
                    }
                } else {
                    resourceFilePathList.add(filePath);
                }
            }
        }
    }

    /**
     * 获取项目路径
     * @return
     */
    public String getProjectPath() throws Exception {
        String result = "";
        if (ArrayUtils.isNotEmpty(javaFilePathList)) {
            result = javaFilePathList.get(0).substring(0, javaFilePathList.get(0).indexOf("src/"));
        } else if (ArrayUtils.isNotEmpty(otherFilePathList)) {
            result = otherFilePathList.get(0).substring(0, otherFilePathList.get(0).indexOf("src/"));
        } else if (ArrayUtils.isNotEmpty(resourceFilePathList)) {
            result = resourceFilePathList.get(0).substring(0, resourceFilePathList.get(0).indexOf("WebContent/"));
        }
        if (result.length() > 0) {
            return result;
        } else {
            throw new Exception("无法获取项目路径！");
        }
    }
}