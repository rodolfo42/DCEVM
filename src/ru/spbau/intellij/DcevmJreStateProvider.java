package ru.spbau.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;


/**
 * User: user
 * Date: 4/10/13
 * Time: 4:46 PM
 */
public class DcevmJreStateProvider implements ApplicationComponent {
    private boolean dcevmReady;

    public DcevmJreStateProvider() {
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "DcevmJreStateProvider";
    }

    public boolean isDcevmReady() {
        if (!dcevmReady) {
            dcevmReady = validateDcevmJre();
        }
        return dcevmReady;
    }

    private boolean validateDcevmJre() {
        // some validation
        return false;
    }

}
