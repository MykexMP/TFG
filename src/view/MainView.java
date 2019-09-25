package view;

import model.Util;
import model.algorithm.Algorithm;
import model.algorithm.AlgorithmFactory;
import model.algorithm.AlgorithmMostPromising;
import model.compiler.CompilerFactory;
import model.compiler.CompilerTime;
import model.compiler.Compiler;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import static model.Util.*;

public class MainView extends JFrame {
    private JComboBox priority;
    private JComboBox algorithm;
    private JLabel labelPriority;
    private JLabel labelThreshold;
    private JPanel rootPanel;
    private JLabel welcomeLabel;
    private JButton compileButton;
    private JTextField pathFile;
    private JButton fileExplorer;
    private JTextField libraries;
    private JLabel labelLibraries;

    private Algorithm a = AlgorithmMostPromising.getAlgorithm();
    private AlgorithmFactory af = new AlgorithmFactory();

    private Compiler c = CompilerTime.getCompiler();
    private CompilerFactory cf = new CompilerFactory();

    private List<String> flagsC = Util.getCFlags();
    private List<String> flagsCPP = Util.getCPPFlags();

    public MainView()
    {
        initWindow();

        libraries.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                libraries.setText("");
            }
        });

        fileExplorer.addActionListener(i -> {
            fileExplorer();
            refreshCompileButton();
        });

        priority.addItemListener(e -> {
            if(e.getStateChange()== ItemEvent.SELECTED){
                c = cf.getCompilator((String)e.getItem());
                refreshCompileButton();
            }
        });

        algorithm.addItemListener(e -> {
            if(e.getStateChange()== ItemEvent.SELECTED){
                a = af.getAlgorithm((String)e.getItem());
                refreshCompileButton();
            }
        });

        pathFile.setDropTarget(new DropTarget() {
            public void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f: files ){
                        checkFile(f);
                    }
                    refreshCompileButton();
                } catch (Exception e) {}
            }
        });
    }

    private void initWindow()
    {
        add(rootPanel);

        compileButton.setVisible(false);

        setTitle("Compilador Inteligente");
        setSize(600,400);
    }

    private void refreshCompileButton() {
        for (ActionListener al : compileButton.getActionListeners()) {
            compileButton.removeActionListener(al);
        }
        if(pathFile.getText().charAt(pathFile.getText().length()-1)=='c'){
            compileButton.addActionListener(i -> c.compile(pathFile.getText(),libraries.getText(),flagsC,c,a));
        }else{
            compileButton.addActionListener(i -> c.compile(pathFile.getText(),libraries.getText(),flagsCPP,c,a));
        }
    }

    private void fileExplorer()
    {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Ficheros C y C++", "c", "cpp");
        fc.setCurrentDirectory(new File(System.getProperty("user.home") + "\\Desktop"));
        fc.setFileFilter(filter);
        fc.showOpenDialog(this);

        File f = fc.getSelectedFile();
        if (f!=null) checkFile(f);
    }

    private void checkFile(File f)
    {
        if(isFileCompatible(f.getAbsolutePath())) {
            pathFile.setText(f.getAbsolutePath());
            compileButton.setVisible(true);
            pathFile.setEditable(false);
        }
        else {
            pathFile.setText("Suelta aquí el fichero a compilar...");
            compileButton.setVisible(false);
            pathFile.setEditable(true);
        }
    }
}