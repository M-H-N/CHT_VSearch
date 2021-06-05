package commons.gui.parent;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class DialogCustom extends JDialog implements WindowListener {

    public DialogCustom() {
        this.setContentPane(this.getRootPane());
        this.setModal(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        this.pack();
    }

    public void open(String title) {
        // call onCancel() on ESCAPE
        this.getRootPanel().registerKeyboardAction(e -> this.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        this.setTitle(title == null ? "" : title);
        this.setVisible(true);
        this.onResume();
    }

    protected abstract JPanel getRootPanel();

    protected void onResume() {
    }

    protected void onClosing() {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.onClosing();
        this.dispose();
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}
