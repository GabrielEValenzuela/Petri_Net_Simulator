package org.petrinator.editor.actions.algorithms;

import org.petrinator.editor.Root;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 * @brief
 */
public class ClassificationAction extends AbstractAction
{
    public ClassificationAction(Root root)
    {
        String name = "Net classification";
        putValue(NAME, name);
    }
    public void actionPerformed(ActionEvent e) {}
}
