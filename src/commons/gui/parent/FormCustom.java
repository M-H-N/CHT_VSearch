package commons.gui.parent;

import javax.swing.*;
import java.awt.event.*;

public abstract class FormCustom implements
        WindowListener,
        ComponentListener,
        FocusListener,
        WindowStateListener {

    protected JFrame rootFrame = null;


    public void show() {
        this.show(false);
    }

    public void show(boolean isRoot) {
        this.show(800, 600, isRoot);
    }

    public void show(int width, int height, boolean isRootWindow) {
        this.rootFrame = new JFrame(this.getTitle());
        this.rootFrame.setContentPane(this.getRootPanel());
        if (isRootWindow) this.rootFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        else this.rootFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.rootFrame.addWindowListener(this);
        this.rootFrame.addComponentListener(this);
        this.rootFrame.addFocusListener(this);
        this.rootFrame.addWindowStateListener(this);
        this.rootFrame.setSize(width, height);
        this.rootFrame.setVisible(true);
    }

    public void close() {
        this.rootFrame.dispatchEvent(new WindowEvent(this.rootFrame, WindowEvent.WINDOW_CLOSING));
    }

    public abstract String getTitle();

    public abstract JPanel getRootPanel();

    @Override
    public void windowOpened(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowClosing(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowClosed(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowIconified(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowActivated(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void focusGained(FocusEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void focusLost(FocusEvent e) {
        // Just to see what we have :)
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        // Just to see what we have :)
    }
}
