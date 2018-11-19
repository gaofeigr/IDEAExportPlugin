package com.gaofeigr.compiler;

import com.gaofeigr.model.CompilerInfoModel;
import com.gaofeigr.utils.ArrayUtils;
import com.gaofeigr.utils.CompilerUtils;
import com.gaofeigr.utils.FileUtil;
import com.intellij.openapi.actionSystem.DataContext;
import org.apache.commons.httpclient.util.DateUtil;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class CompilerFiles {

    private CompilerInfoModel compilerInfoModel;

    public CompilerFiles(DataContext dataContext, String outPutPath) {
        if (compilerInfoModel == null) {
            compilerInfoModel = new CompilerInfoModel(dataContext, outPutPath);
        }
    }


    /**
     * 导出编译后的java文件
     */
    public void execExport() {
        try {
            //编译并输出文件
            compilerFiles();
        } catch (Exception e) {
            System.err.println("编译失败！");
        } finally {
            compilerInfoModel.getJavaFilePathList().clear();
            compilerInfoModel.getOtherFilePathList().clear();
            compilerInfoModel.getResourceFilePathList().clear();
        }
    }

    /**
     * 编译文件
     */
    //todo: 重写此方法，所有参数改成动态
    private void compilerFiles() throws Exception {
        String projectPath = compilerInfoModel.getCurrentProjectPath();
        String outPutPath = compilerInfoModel.getOutPutPath() + "\\result\\result" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
        String classFileOutPutPath = outPutPath + "\\WEB-INF\\classes";
        FileUtil.createFolder(outPutPath);
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getJavaFilePathList())) {
            FileUtil.createFolder(classFileOutPutPath);
            compiler(classFileOutPutPath);
        }
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getOtherFilePathList())) {
            FileUtil.copyOtherFiles(compilerInfoModel.getOtherFilePathList(), projectPath+"src", classFileOutPutPath);
        }
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getResourceFilePathList())) {
            FileUtil.copyResourceFiles(compilerInfoModel.getResourceFilePathList(), projectPath+"WebContent", outPutPath);
        }
    }

    //todo: 重写此方法，所有参数改成动态
    private void compiler(String outPutPath) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        fileManager.handleOption("-classpath",
                Arrays.asList(CompilerUtils.buildClassPath(compilerInfoModel.getCurrentModuleLibrariesPath())).iterator());
        fileManager.handleOption("-encoding",
                Arrays.asList("UTF8").iterator());
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File[] { new File(outPutPath) }));
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(compilerInfoModel.getJavaFilePathList());
            List<String> test = new ArrayList<String>();
            test.add("-source");
            test.add("1.6");
            test.add("-target");
            test.add("1.6");
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, test, null, compilationUnits);
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
}