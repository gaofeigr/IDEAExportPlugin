package com.gaofeigr.actions;

import com.gaofeigr.dialogs.ExportDialog;
import com.intellij.openapi.actionSystem.*;

public class ExportFilesAction extends com.intellij.openapi.actionSystem.AnAction {

    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            ExportDialog dialog = new ExportDialog();
            dialog.setFile(anActionEvent.getDataContext());
            dialog.pack();
            dialog.setTitle("ExportFiles");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.toString();
        }
    }
}