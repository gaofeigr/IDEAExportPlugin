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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CompilerFiles {

    private CompilerInfoModel compilerInfoModel;

    private interface Options {
        String CLASSPATH = "-classpath";
        String ENCODING = "-encoding";
        String SOURCE = "-source";
        String TARGET = "-target";
    }

    private interface Encodings {
        String UTF_8 = "UTF8";
    }


    public CompilerFiles(DataContext dataContext, String outPutPath) {
        if (compilerInfoModel == null) {
            compilerInfoModel = new CompilerInfoModel(dataContext, outPutPath);
        }
    }


    /**
     * 导出编译后的java文件
     */
    public CompilerInfoModel execExport() {
        try {
            //编译并输出文件
            compilerFiles();
        } catch (Exception e) {
            System.err.println("编译失败！");
        } finally {
            compilerInfoModel.getJavaFilePathList().clear();
            compilerInfoModel.getOtherFilePathList().clear();
            compilerInfoModel.getResourceFilePathList().clear();
            return compilerInfoModel;
        }
    }

    /**
     * 编译文件
     */
    //todo: 重写此方法，所有参数改成动态
    private void compilerFiles() throws Exception {
        String projectPath = compilerInfoModel.getCurrentProjectPath();
        String outPutPath = compilerInfoModel.getOutPutPath() + "\\result";
        String classFileOutPutPath = outPutPath + "\\WEB-INF\\classes";
        FileUtil.createFolder(outPutPath);
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getJavaFilePathList())) {
            FileUtil.createFolder(classFileOutPutPath);
            compilerInfoModel.setSuccessJavaFileNum(compiler(classFileOutPutPath));
        }
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getOtherFilePathList())) {
            compilerInfoModel.setSuccessOtherFileNum(FileUtil.copyOtherFiles(compilerInfoModel.getOtherFilePathList(), projectPath+File.separator+"src", classFileOutPutPath));
        }
        if (ArrayUtils.isNotEmpty(compilerInfoModel.getResourceFilePathList())) {
            compilerInfoModel.setSuccessResourceFileNum(FileUtil.copyResourceFiles(compilerInfoModel.getResourceFilePathList(), projectPath+File.separator+"WebContent", outPutPath));
        }
        compilerInfoModel.setSuccessNum(compilerInfoModel.getSuccessJavaFileNum()+compilerInfoModel.getSuccessOtherFileNum()+compilerInfoModel.getSuccessResourceFileNum());
    }

    //todo: 重写此方法，所有参数改成动态
    private int compiler(String outPutPath) throws IOException {
        int result = 0;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        fileManager.handleOption(Options.CLASSPATH,
                Arrays.asList(CompilerUtils.buildClassPath(compilerInfoModel.getCurrentModuleLibrariesPath())).iterator());
        fileManager.handleOption(Options.ENCODING,
                Arrays.asList(Encodings.UTF_8).iterator());
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
                Arrays.asList(new File[] { new File(outPutPath) }));
        try {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(compilerInfoModel.getJavaFilePathList());
            List<String> options = Arrays.asList(Options.SOURCE, compilerInfoModel.getCurrentProjectSDKVersion()
                    , Options.TARGET, compilerInfoModel.getCurrentProjectSDKVersion());
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
            if (!task.call()) {
                outErrorMessage(diagnostics);
            } else {
                result = compilerInfoModel.getJavaFilePathList().size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileManager != null) {
                fileManager.close();
            }
            return result;
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