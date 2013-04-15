package ru.spbau.intellij;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import ru.spbau.dcevm.Downloader;
import ru.spbau.host.LocalUserAddressProvider;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: yarik
 * Date: 4/15/13
 * Time: 3:54 AM
 */
public class Starter implements StartupActivity {

    private static final AtomicBoolean dcevmDownloadInProcess = new AtomicBoolean(false);

    @Override
    public void runActivity(final Project project) {

        int result = Messages.showYesNoDialog("Download dcevm?", "DCEVM plugin", null);
        if (result == 0) {
            new Task.Backgroundable(project, "DCEVM plugin", true) {
                @Override
                public void run(ProgressIndicator indicator) {
                    indicator.setText("Downloading DCEVM jre");
                    Downloader dcevmLoader = new Downloader(LocalUserAddressProvider.getDcevmHomeAddress());
                    try {
                        dcevmLoader.downloadDcevm(indicator);
                    } catch (IOException e) {
                        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
                        JBPopupFactory.getInstance()
                                .createHtmlTextBalloonBuilder("<html>DCEVM download:<br>IO Error during downloading</html>", MessageType.ERROR, null)
                                .setFadeoutTime(7500)
                                .createBalloon()
                                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                        Balloon.Position.atRight);
                    }
                }

                @Override
                public void onSuccess() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                            for (Project project: openProjects) {
                                changeTemplateAlterJre(project, LocalUserAddressProvider.getDcevmHomeAddress(), true);
                            }
                        }
                    });
                }

                @Override
                public void onCancel() {
                    // delete DCEVM home directory hoya!
                }

            }.setCancelText("Stop downloading").queue();
        }
    }

    private void changeTemplateAlterJre(Project project, String alterJrePath, boolean enabled) {
        RunManagerImpl runManager = (RunManagerImpl)RunManagerImpl.getInstance(project);
        ConfigurationFactory factory = ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
        ApplicationConfiguration templateApplicationConfig = (ApplicationConfiguration)
                runManager.getConfigurationTemplate(factory).getConfiguration();
        templateApplicationConfig.ALTERNATIVE_JRE_PATH = alterJrePath;
        templateApplicationConfig.ALTERNATIVE_JRE_PATH_ENABLED = enabled;
    }

    private boolean isDcevmInstalled() {

    }



}
