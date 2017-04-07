package org.petrinator.auxiliar;

import javax.swing.*;
import java.awt.*;
import org.petrinator.editor.Root;

/**
 * @author Joaquin Felici <joaquinfelici at gmail.com>
 */
public class ProgressBarDialog
{
    private JDialog dialog;

    public ProgressBarDialog(Root root, String message)
    {
        dialog = new JDialog(root.getParentFrame(), false); // We set it modal so it doesn't interrupt the thread
        dialog.setUndecorated(false);
        dialog.setLocationRelativeTo(root.getParentFrame());
        dialog.setTitle("Petrinator");

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(200, 25));
        bar.setBorderPainted(true);
        bar.setString(message);

        dialog.add(bar);
        dialog.pack();
    }

    public void show(boolean b)
    {
        dialog.setVisible(b);
    }
}
