package cht_v_search.gui;

import javax.swing.*;
import java.awt.*;

public class DialogLoading extends JDialog {
    private JPanel panelRoot;
    private JLabel txtLoadingTitle;
    private JLabel txtDescription;

    public DialogLoading() {
        this("");
    }

    public DialogLoading(final String description) {
        this.setContentPane(this.panelRoot);
        this.setModal(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setSize(400, 150);
//        this.pack();
        this.txtDescription.setText(description);
    }

    public void startAnimation() {
        //// TODO: implement later
    }

    public void showIt() {
        this.showIt("");
    }

    public void showIt(final String description) {
        this.showIt(description, null);
    }

    public void showIt(final String description, final Component parent) {
        if (parent != null) this.setLocationRelativeTo(parent);
        this.txtDescription.setText(description);
        this.setVisible(true);
    }

    public void hideIt() {
        this.setVisible(false);
    }


    public void setDescription(final String description) {
        this.txtDescription.setText(description);
    }

}
