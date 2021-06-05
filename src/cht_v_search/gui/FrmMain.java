package cht_v_search.gui;

import cht_v_search.core.VSearch;
import cht_v_search.domain.VResult;
import cht_v_search.utils.Utility;
import commons.gui.parent.FormCustom;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FrmMain extends FormCustom {
    //    private JSpinner spinnerMaxWordsBound;
//    private JSpinner spinnerMaxCharsBound;
    private final DialogLoading dialogLoading = new DialogLoading();
    private final FileFilter fileChooserFileFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.getName().toLowerCase(Locale.ENGLISH).endsWith(".txt") ||
                    f.getName().toLowerCase(Locale.ENGLISH).endsWith(".pdf");
        }

        @Override
        public String getDescription() {
            return "PDF or TXT files only!";
        }
    };
    private JPanel panelMain;
    private JTextField txtQuestion;
    //    private JTextArea txtAnswers;
    private JButton btnBrowseSourceFile;
    private JScrollPane scrollPaneAnswers;
    private JButton btnSearch;
    private JList<VResult> listResult;
    private VSearch vSearch = null;

    public FrmMain() {
        this.initializeViews();
    }

    private void initializeViews() {
        this.btnBrowseSourceFile.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(this.fileChooserFileFilter);
            if (fileChooser.showOpenDialog(this.getRootPanel()) == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null && selectedFile.isFile()) {
                    new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            try {
                                dialogLoading.showIt("Loading file...", getRootPanel());
                                if (selectedFile.getName().toLowerCase().endsWith(".txt")) {
                                    SwingUtilities.invokeLater(() -> dialogLoading.showIt("Loading txt file...", getRootPanel()));
                                    vSearch = new VSearch(selectedFile);
                                } else if (selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                                    SwingUtilities.invokeLater(() -> dialogLoading.showIt("Loading pdf file...", getRootPanel()));
                                    vSearch = new VSearch(Utility.extractAllTextsInPdf(selectedFile));
                                }
                                btnBrowseSourceFile.setText(fileChooser.getSelectedFile().getName());
                            } catch (IOException ioException) {
                                vSearch = null;
                                ioException.printStackTrace();
                            }
                            SwingUtilities.invokeLater(dialogLoading::hideIt);
                            return null;
                        }
                    }.execute();
                }
            }
        });
        this.txtQuestion.addActionListener(e -> this.onSearch());
        this.btnSearch.addActionListener(e -> this.onSearch());
        this.txtQuestion.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
    }

    private void onSearch() {
        System.out.println("onTextChanged: " + this.txtQuestion.getText());
        if (this.vSearch == null || this.txtQuestion.getText() == null || this.txtQuestion.getText().isEmpty() || this.txtQuestion.getText().isBlank()) {
//            this.txtAnswers.setText("Source file not chosen!");
            return;
        }
        this.dialogLoading.showIt("Searching...");
        this.vSearch.search(this.txtQuestion.getText(), vResultList -> {
            final ListModel<VResult> listModel = new AbstractListModel<VResult>() {
                @Override
                public int getSize() {
                    return vResultList.size();
                }

                @Override
                public VResult getElementAt(int index) {
                    return vResultList.get(index);
                }
            };

            this.listResult.setCellRenderer(new ResultListRenderer());
            this.listResult.setModel(listModel);

            this.listResult.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        final int index = listResult.locationToIndex(e.getPoint());

                        final JTextArea msg = new JTextArea(vResultList.get(index).getTextLong());
                        msg.setFont(msg.getFont().deriveFont(40F));
                        msg.setLineWrap(true);
                        msg.setWrapStyleWord(true);

                        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
                        vResultList.get(index).getWords().forEach(word -> {
                            try {
                                final int length = word.getText().trim().length();
                                List<Integer> indices = Utility.getAllOccurrencesOf(vResultList.get(index).getTextLong(), word.getText().trim());
                                for (Integer integer : indices)
                                    msg.getHighlighter().addHighlight(integer, integer + length, painter);
                            } catch (BadLocationException ee) {
                                ee.printStackTrace();
                            }
                        });


                        JScrollPane scrollPane = new JScrollPane(msg);
                        scrollPane.setSize(800, 600);

                        JOptionPane jOptionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);
                        jOptionPane.setSize(800, 600);
//                        jOptionPane.show();
                        jOptionPane.setVisible(true);
                    }
                }
            });

            this.scrollPaneAnswers.getVerticalScrollBar().setValue(0);
            this.dialogLoading.hideIt();

        });
    }

    public JPanel getPanelMain() {
        return panelMain;
    }

    @Override
    public String getTitle() {
        return "CHT_VSearch";
    }

    @Override
    public JPanel getRootPanel() {
        return this.panelMain;
    }
}
