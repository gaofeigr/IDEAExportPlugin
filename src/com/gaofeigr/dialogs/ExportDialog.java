package com.gaofeigr.dialogs;

import com.gaofeigr.compiler.CompilerFiles;
import com.gaofeigr.model.CompilerInfoModel;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class ExportDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField inputOutPath;
    private JButton buttonOutPath;
    private JRadioButton CompressRadioButton_no;
    private JRadioButton CompressRadioButton_yes;

    private DataContext dataContext;

    public void setFile(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public ExportDialog() throws IOException {
        inputOutPath.setText(FileSystemView.getFileSystemView().getHomeDirectory().toString());
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        buttonOutPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                choosePath();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 选择导出目录
     */
    private void choosePath() {
        JFileChooser jfc = new JFileChooser();
        //设置当前路径为桌面路径,否则将我的文档作为默认路径
        jfc.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        //JFileChooser.FILES_AND_DIRECTORIES 选择路径和文件
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //弹出的提示框的标题
        jfc.showDialog(new JLabel(), "确定");
        //用户选择的路径或文件
        File file = jfc.getSelectedFile();
        inputOutPath.setText(file.getAbsolutePath());
    }

    private void onOK() {
        // add your code here
        CompilerInfoModel result = new CompilerFiles(dataContext, inputOutPath.getText()).execExport();
        dispose();
        Messages.showMessageDialog(result.getProject(),
                String.format("成功导出了%s个文件:\n %s个Java文件\n %s个资源文件\n %s个其他文件(src下的其他文件)", result.getSuccessNum(), result.getSuccessJavaFileNum(), result.getSuccessResourceFileNum(), result.getSuccessOtherFileNum()),
                "导出结果",
                Messages.getInformationIcon());
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
