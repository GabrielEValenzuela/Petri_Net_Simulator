package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief
 */
public class SafetyAction extends AbstractAction
{
    public SafetyAction(Root root)
    {
        String name = "Net safety parameters";
        putValue(NAME, name);
    }
    public void actionPerformed(ActionEvent e) {}
}
