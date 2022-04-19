package com.gaofeigr.model;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EnvironmentUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 编译所需基本信息model
 *///
public class CompilerInfoModel {

    public CompilerInfoModel(DataContext dataContext, String outPutPath) {
        initModelInfo(dataContext, outPutPath);
    }

    /**
     * 初始化编译信息
     */
    private void initModelInfo(DataContext dataContext, String outPutPath) {
        this.project = dataContext.getData(CommonDataKeys.PROJECT);
        this.dataContext = dataContext;
        this.outPutPath = outPutPath;
        this.selectFiles = dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        this.currentProjectPath = this.project.getBasePath().replaceAll("/", "\\\\");
        this.currentSystemSDKVersion = EnvironmentUtil.getValue("PROCESSOR_LEVEL");
        this.currentProjectSDKVersion = ProjectRootManager.getInstance(project).getProjectSdkName();
        if (this.selectFiles != null && this.selectFiles.length > 0) {
            initCurrentModuleLibrariesPath();
        }

        initFilePathList(this.selectFiles);
    }

    /**
     * 初始化依赖jar目录
     */
    private void initCurrentModuleLibrariesPath() {
        this.getCurrentModuleLibrariesPath().add(".");//add system config path
        Module module = ModuleUtil.findModuleForFile(this.selectFiles[0], this.project);
        ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(library -> {
            for (String libraryPath : library.getUrls(OrderRootType.CLASSES)) {
                if (libraryPath.startsWith("file")) {
                    libraryPath = libraryPath.replace("file://", "").replace("/", File.separator) + File.separator + "*";
                }
                if (libraryPath.startsWith("jar")) {
                    libraryPath = libraryPath.replace("jar://", "").replace("!/", "").replace("/", File.separator);
                }
                this.getCurrentModuleLibrariesPath().add(libraryPath);
            }
            return true;
        });
    }

    /**
     * 初始化文件列表
     * @param selectFiles 选中的文件
     */
    private void initFilePathList(VirtualFile[] selectFiles) {
        for (VirtualFile virtualFile : selectFiles) {
            //若有子文件(即选中的是目录)则遍历子文件
            if (virtualFile.getChildren() != null && virtualFile.getChildren().length != 0) {
                initFilePathList(virtualFile.getChildren());
            } else {
                String filePath = virtualFile.getCanonicalPath();
                //添加文件绝对路径
                if (filePath.contains("src")) {
                    if (filePath.endsWith(".java")) {
                        this.getJavaFilePathList().add(filePath);
                    } else {
                        this.getOtherFilePathList().add(filePath);
                    }
                } else {
                    this.getResourceFilePathList().add(filePath);
                }
            }
        }
    }

    /**
     * 项目信息
     */
    private Project project;

    /**
     * 数据上下文
     */
    private DataContext dataContext;

    /**
     * 输出目录
     */
    private String outPutPath;

    /**
     * 所有选中的java文件列表
     */
    private List<String> javaFilePathList;

    /**
     * 其他类型文件列表
     */
    private List<String> otherFilePathList;

    /**
     * 资源文件列表
     */
    private List<String> resourceFilePathList;

    /**
     * 当前项目路径
     */
    private String currentProjectPath;

    /**
     * 当前系统SDK版本
     */
    private String currentSystemSDKVersion;

    /**
     * 当前项目SDK版本
     */
    private String currentProjectSDKVersion;

    /**
     * 当前模块引用Libraries路径
     */
    private List<String> currentModuleLibrariesPath;

    /**
     * 选中的文件列表
     */
    VirtualFile[] selectFiles;

    /**
     * 成功导出文件数量
     */
    private int successNum;

    private int successJavaFileNum;

    private int successOtherFileNum;

    private int successResourceFileNum;

    public int getSuccessJavaFileNum() {
        return successJavaFileNum;
    }

    public void setSuccessJavaFileNum(int successJavaFileNum) {
        this.successJavaFileNum = successJavaFileNum;
    }

    public int getSuccessOtherFileNum() {
        return successOtherFileNum;
    }

    public void setSuccessOtherFileNum(int successOtherFileNum) {
        this.successOtherFileNum = successOtherFileNum;
    }

    public int getSuccessResourceFileNum() {
        return successResourceFileNum;
    }

    public void setSuccessResourceFileNum(int successResourceFileNum) {
        this.successResourceFileNum = successResourceFileNum;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public VirtualFile[] getSelectFiles() {
        return selectFiles;
    }

    public void setSelectFiles(VirtualFile[] selectFiles) {
        this.selectFiles = selectFiles;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public void setDataContext(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public String getOutPutPath() {
        return outPutPath;
    }

    public void setOutPutPath(String outPutPath) {
        this.outPutPath = outPutPath;
    }

    public List<String> getJavaFilePathList() {
        if (this.javaFilePathList == null) {
            this.javaFilePathList = new ArrayList<String>();
        }
        return javaFilePathList;
    }

    public void setJavaFilePathList(List<String> javaFilePathList) {
        this.javaFilePathList = javaFilePathList;
    }

    public List<String> getOtherFilePathList() {
        if (this.otherFilePathList == null) {
            this.otherFilePathList = new ArrayList<String>();
        }
        return otherFilePathList;
    }

    public void setOtherFilePathList(List<String> otherFilePathList) {
        this.otherFilePathList = otherFilePathList;
    }

    public List<String> getResourceFilePathList() {
        if (this.resourceFilePathList == null) {
            this.resourceFilePathList = new ArrayList<String>();
        }
        return resourceFilePathList;
    }

    public void setResourceFilePathList(List<String> resourceFilePathList) {
        this.resourceFilePathList = resourceFilePathList;
    }

    public String getCurrentProjectPath() {
        return currentProjectPath;
    }

    public void setCurrentProjectPath(String currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public String getCurrentSystemSDKVersion() {
        return currentSystemSDKVersion;
    }

    public void setCurrentSystemSDKVersion(String currentSystemSDKVersion) {
        this.currentSystemSDKVersion = currentSystemSDKVersion;
    }

    public String getCurrentProjectSDKVersion() {
        return currentProjectSDKVersion;
    }

    public void setCurrentProjectSDKVersion(String currentProjectSDKVersion) {
        this.currentProjectSDKVersion = currentProjectSDKVersion;
    }

    public List<String> getCurrentModuleLibrariesPath() {
        if (this.currentModuleLibrariesPath == null) {
            this.currentModuleLibrariesPath = new ArrayList<String>();
        }
        return currentModuleLibrariesPath;
    }

    public void setCurrentModuleLibrariesPath(List<String> currentModuleLibrariesPath) {
        this.currentModuleLibrariesPath = currentModuleLibrariesPath;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}