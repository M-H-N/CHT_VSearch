package cht_v_search.gui;

import cht_v_search.domain.VResult;
import cht_v_search.utils.Utility;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.List;

public class ResultListRenderer implements ListCellRenderer<VResult> {
    private final JPanel jPanel;
    private final JTextArea textArea;

    public ResultListRenderer() {
        final BorderLayout borderLayout = new BorderLayout();

        final Border border = new LineBorder(Color.BLACK);

        this.jPanel = new JPanel(borderLayout);
        this.jPanel.setBorder(border);

        this.textArea = new JTextArea();
        this.textArea.setMargin(new Insets(5, 5, 5, 5));
        this.textArea.setFont(this.textArea.getFont().deriveFont(26F));
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        this.jPanel.add(this.textArea, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends VResult> list, VResult value, int index, boolean isSelected, boolean cellHasFocus) {
//        final String txt = (isSelected || cellHasFocus) ? value.getTextLong() : value.getText();
        final String txt = value.getText();
        this.textArea.setText(txt);
//        list.setFixedCellHeight(200);

        int width = list.getWidth();

        if (width > 0) this.textArea.setSize(width, Short.MAX_VALUE);

        this.textArea.setBackground(cellHasFocus ? Color.LIGHT_GRAY : Color.WHITE);
        this.textArea.setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);

        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        value.getWords().forEach(word -> {
            try {
//                final int startIndexHighlight = word.getTextIndex() - value.getTextStartIndex();
//                final int endIndexHighlight = startIndexHighlight + word.getText().length();
                final int length = word.getText().trim().length();
                List<Integer> indices = Utility.getAllOccurrencesOf(txt, word.getText().trim());
                for (Integer integer : indices)
                    this.textArea.getHighlighter().addHighlight(integer, integer + length, painter);

//                this.textArea.getHighlighter().addHighlight(startIndexHighlight, endIndexHighlight, painter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
        return this.jPanel;
    }
}
