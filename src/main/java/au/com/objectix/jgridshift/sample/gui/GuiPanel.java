/*
 * Copyright (c) 2003 Objectix Pty Ltd  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL OBJECTIX PTY LTD BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package au.com.objectix.jgridshift.sample.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import au.com.objectix.jgridshift.GridShift;

/**
 * @author peter
 */
public class GuiPanel extends JPanel {

  private static final long serialVersionUID = -4438339666831365487L;
  private static final String NOT_GSF = "Not a valid GridShift File";
  private static final String DEGREE = "Degrees (Positive East Lon)";
  private static final String SECOND = "Seconds (Positive West Lon)";
  private static final String DMS = "DMS (Positive East Lon)";
  private static final Integer ZERO = new Integer(0);
  private Object[] display = new Object[] {DEGREE,SECOND,DMS};
  private EntryFocus entryFocus = new EntryFocus();
  private JPanel filePanel = new JPanel();
  private JLabel gridShiftFileLabel = new JLabel("Grid Shift File: ");
  private JTextField gridShiftFileName = new JTextField(20);
  private JButton fileButton = new JButton("...");
  private JLabel gridShiftErrorLabel = new JLabel(" ");
  private JLabel filler1 = new JLabel(" ");
  private JLabel filler2 = new JLabel("         ");
  private JFileChooser fileChooser = new JFileChooser();
  private Gui gui;
  private GridShift gridShift = new GridShift();

  private JPanel fromPanel = new JPanel(new BorderLayout());
  private JLabel fromLabel = new JLabel("From Coordinate  ");
  private JComboBox fromCombo = new JComboBox(display);
  private CardLayout fromCardLayout = new CardLayout();
  private JPanel fromCardPanel = new JPanel(fromCardLayout);
  private GridLayout fromDegreeLayout = new GridLayout(2,2);
  private JPanel fromDegreePanel = new JPanel(fromDegreeLayout);
  private JLabel fromDegreeLonLabel = new JLabel("Lon: ");
  private JLabel fromDegreeLatLabel = new JLabel("Lat: ");
  private DecimalFormat degreeDecimalFormat = new DecimalFormat("##0.000000000");
  private JTextField fromDegreeLonText = new JTextField();
  private JTextField fromDegreeLatText = new JTextField();
  private GridLayout fromSecondLayout = new GridLayout(2,2);
  private JPanel fromSecondPanel = new JPanel(fromSecondLayout);
  private JLabel fromSecondLonLabel = new JLabel("Lon: ");
  private JLabel fromSecondLatLabel = new JLabel("Lat: ");
  private DecimalFormat secondDecimalFormat = new DecimalFormat("######0.00000");
  private JTextField fromSecondLonText = new JTextField();
  private JTextField fromSecondLatText = new JTextField();
  private GridLayout fromDMSLayout = new GridLayout(2,4);
  private JPanel fromDMSPanel = new JPanel(fromDMSLayout);
  private JLabel fromDMSLonLabel = new JLabel("Lon: ");
  private JLabel fromDMSLatLabel = new JLabel("Lat: ");
  private DecimalFormat degMinDecimalFormat = new DecimalFormat("##0");
  private JTextField fromDMSDegLonText = new JTextField();
  private JTextField fromDMSDegLatText = new JTextField();
  private JTextField fromDMSMinLonText = new JTextField();
  private JTextField fromDMSMinLatText = new JTextField();
  private JTextField fromDMSSecLonText = new JTextField();
  private JTextField fromDMSSecLatText = new JTextField();

  private JPanel toPanel = new JPanel(new BorderLayout());
  private JLabel toLabel = new JLabel("To Coordinate    ");
  private JComboBox toCombo = new JComboBox(display);
  private CardLayout toCardLayout = new CardLayout();
  private JPanel toCardPanel = new JPanel(toCardLayout);
  private GridLayout toDegreeLayout = new GridLayout(2,2);
  private JPanel toDegreePanel = new JPanel(toDegreeLayout);
  private JLabel toDegreeLonLabel = new JLabel("Lon: ");
  private JLabel toDegreeLatLabel = new JLabel("Lat: ");
  private JFormattedTextField toDegreeLonText = new JFormattedTextField(degreeDecimalFormat);
  private JFormattedTextField toDegreeLatText = new JFormattedTextField(degreeDecimalFormat);
  private GridLayout toSecondLayout = new GridLayout(2,2);
  private JPanel toSecondPanel = new JPanel(toSecondLayout);
  private JLabel toSecondLonLabel = new JLabel("Lon: ");
  private JLabel toSecondLatLabel = new JLabel("Lat: ");
  private JFormattedTextField toSecondLonText = new JFormattedTextField(secondDecimalFormat);
  private JFormattedTextField toSecondLatText = new JFormattedTextField(secondDecimalFormat);
  private GridLayout toDMSLayout = new GridLayout(2,4);
  private JPanel toDMSPanel = new JPanel(toDMSLayout);
  private JLabel toDMSLonLabel = new JLabel("Lon: ");
  private JLabel toDMSLatLabel = new JLabel("Lat: ");
  private JFormattedTextField toDMSDegLonText = new JFormattedTextField(degMinDecimalFormat);
  private JFormattedTextField toDMSDegLatText = new JFormattedTextField(degMinDecimalFormat);
  private JFormattedTextField toDMSMinLonText = new JFormattedTextField(degMinDecimalFormat);
  private JFormattedTextField toDMSMinLatText = new JFormattedTextField(degMinDecimalFormat);
  private JFormattedTextField toDMSSecLonText = new JFormattedTextField(secondDecimalFormat);
  private JFormattedTextField toDMSSecLatText = new JFormattedTextField(secondDecimalFormat);

  private JLabel message = new JLabel(" ");
  private JButton forwardButton = new JButton(Gui.NOT_LOADED);
  private JButton reverseButton = new JButton(Gui.NOT_LOADED);

  public GuiPanel(String gridShiftFile, GridBagLayout gridbag, Gui gui) throws Exception {
    super(gridbag);
    this.gui = gui;
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.CENTER;
    filePanel.add(gridShiftFileLabel);
    gridShiftFileName.setText(gridShiftFile);
    FileVerifier fileVerifier = new FileVerifier();
    gridShiftFileName.setInputVerifier(fileVerifier);
    filePanel.add(gridShiftFileName);
    filePanel.add(fileButton);
    gridShiftErrorLabel.setForeground(Color.RED);
    fileButton.addActionListener(new FileAction());
    fileChooser.setMultiSelectionEnabled(false);
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(gridShiftErrorLabel, c);
    add(gridShiftErrorLabel);
    gridbag.setConstraints(filePanel, c);
    add(filePanel);
    gridbag.setConstraints(filler1, c);
    add(filler1);

    fromPanel.add(fromLabel, BorderLayout.WEST);
    fromPanel.add(fromCombo, BorderLayout.EAST);
    fromCombo.addActionListener(new FromAction());
    fromPanel.add(fromCardPanel, BorderLayout.SOUTH);
    fromCardPanel.add(fromDegreePanel, DEGREE);
    fromDegreePanel.add(fromDegreeLatLabel);
    fromDegreePanel.add(fromDegreeLatText);
    fromDegreeLatText.setInputVerifier(new FromLatDegreeVerifier());
    fromDegreeLatText.addFocusListener(entryFocus);
    fromDegreePanel.add(fromDegreeLonLabel);
    fromDegreePanel.add(fromDegreeLonText);
    fromDegreeLonText.setInputVerifier(new FromLonDegreeVerifier());
    fromDegreeLonText.addFocusListener(entryFocus);
    fromCardPanel.add(fromSecondPanel, SECOND);
    fromSecondPanel.add(fromSecondLatLabel);
    fromSecondPanel.add(fromSecondLatText);
    fromSecondLatText.setInputVerifier(new FromLatSecondVerifier());
    fromSecondLatText.addFocusListener(entryFocus);
    fromSecondPanel.add(fromSecondLonLabel);
    fromSecondPanel.add(fromSecondLonText);
    fromSecondLonText.setInputVerifier(new FromLonSecondVerifier());
    fromSecondLonText.addFocusListener(entryFocus);
    fromCardPanel.add(fromDMSPanel, DMS);
    fromDMSPanel.add(fromDMSLatLabel);
    fromDMSPanel.add(fromDMSDegLatText);
    fromDMSDegLatText.setInputVerifier(new FromLatDMSDegVerifier());
    fromDMSDegLatText.addFocusListener(entryFocus);
    fromDMSPanel.add(fromDMSMinLatText);
    fromDMSMinLatText.setInputVerifier(new FromLatDMSMinVerifier());
    fromDMSMinLatText.addFocusListener(entryFocus);
    fromDMSPanel.add(fromDMSSecLatText);
    fromDMSSecLatText.setInputVerifier(new FromLatDMSSecVerifier());
    fromDMSSecLatText.addFocusListener(entryFocus);
    fromDMSSecLatText.setColumns(6);
    fromDMSPanel.add(fromDMSLonLabel);
    fromDMSPanel.add(fromDMSDegLonText);
    fromDMSDegLonText.setInputVerifier(new FromLonDMSDegVerifier());
    fromDMSDegLonText.addFocusListener(entryFocus);
    fromDMSPanel.add(fromDMSMinLonText);
    fromDMSMinLonText.setInputVerifier(new FromLonDMSMinVerifier());
    fromDMSMinLonText.addFocusListener(entryFocus);
    fromDMSPanel.add(fromDMSSecLonText);
    fromDMSSecLonText.setInputVerifier(new FromLonDMSSecVerifier());
    fromDMSSecLonText.addFocusListener(entryFocus);
    c.gridwidth = 1;
    gridbag.setConstraints(fromPanel, c);
    add(fromPanel);
    gridbag.setConstraints(filler2, c);
    add(filler2);

    toPanel.add(toLabel, BorderLayout.WEST);
    toPanel.add(toCombo, BorderLayout.EAST);
    toCombo.addActionListener(new ToAction());
    toPanel.add(toCardPanel, BorderLayout.SOUTH);
    toCardPanel.add(toDegreePanel, DEGREE);
    toDegreePanel.add(toDegreeLatLabel);
    toDegreePanel.add(toDegreeLatText);
    toDegreeLatText.setEditable(false);
    toDegreeLatText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDegreePanel.add(toDegreeLonLabel);
    toDegreePanel.add(toDegreeLonText);
    toDegreeLonText.setEditable(false);
    toDegreeLonText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toCardPanel.add(toSecondPanel, SECOND);
    toSecondPanel.add(toSecondLatLabel);
    toSecondPanel.add(toSecondLatText);
    toSecondLatText.setEditable(false);
    toSecondLatText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toSecondPanel.add(toSecondLonLabel);
    toSecondPanel.add(toSecondLonText);
    toSecondLonText.setEditable(false);
    toSecondLonText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toCardPanel.add(toDMSPanel, DMS);
    toDMSPanel.add(toDMSLatLabel);
    toDMSPanel.add(toDMSDegLatText);
    toDMSDegLatText.setEditable(false);
    toDMSDegLatText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDMSPanel.add(toDMSMinLatText);
    toDMSMinLatText.setEditable(false);
    toDMSMinLatText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDMSPanel.add(toDMSSecLatText);
    toDMSSecLatText.setEditable(false);
    toDMSSecLatText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDMSSecLatText.setColumns(6);
    toDMSPanel.add(toDMSLonLabel);
    toDMSPanel.add(toDMSDegLonText);
    toDMSDegLonText.setEditable(false);
    toDMSDegLonText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDMSPanel.add(toDMSMinLonText);
    toDMSMinLonText.setEditable(false);
    toDMSMinLonText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    toDMSPanel.add(toDMSSecLonText);
    toDMSSecLonText.setEditable(false);
    toDMSSecLonText.setFocusLostBehavior(JFormattedTextField.PERSIST);
    c.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(toPanel, c);
    add(toPanel);

    gridbag.setConstraints(message, c);
    add(message);
    forwardButton.setEnabled(false);
    forwardButton.addActionListener(new ForwardAction());
    gridbag.setConstraints(forwardButton, c);
    add(forwardButton);
    reverseButton.setEnabled(false);
    reverseButton.addActionListener(new ReverseAction());
    gridbag.setConstraints(reverseButton, c);
    add(reverseButton);

    if (gridShiftFile != null) {
      fileVerifier.verify(gridShiftFileName);
    }
  }

  public void clearToValues() {
    toDegreeLonText.setText("");
    toSecondLonText.setText("");
    toDMSDegLonText.setText("");
    toDMSMinLonText.setText("");
    toDMSSecLonText.setText("");

    toDegreeLatText.setText("");
    toSecondLatText.setText("");
    toDMSDegLatText.setText("");
    toDMSMinLatText.setText("");
    toDMSSecLatText.setText("");

    message.setText(" ");
  }

  public void setToValues() {
    toDegreeLonText.setValue(new Double(gridShift.getShiftedLonPositiveEastDegrees()));
    toSecondLonText.setValue(new Double(gridShift.getShiftedLonPositiveWestSeconds()));
    int deg = (int)(gridShift.getShiftedLonPositiveWestSeconds() / -3600.0);
    int min = (int)(Math.abs(gridShift.getShiftedLonPositiveWestSeconds()) / 60.0);
    double sec = Math.abs(gridShift.getShiftedLonPositiveWestSeconds()) - (min * 60.0);
    min = min % 60;
    toDMSDegLonText.setValue(new Integer(deg));
    toDMSMinLonText.setValue(new Integer(min));
    toDMSSecLonText.setValue(new Double(sec));

    toDegreeLatText.setValue(new Double(gridShift.getShiftedLatDegrees()));
    toSecondLatText.setValue(new Double(gridShift.getShiftedLatSeconds()));
    deg = (int)(gridShift.getShiftedLatSeconds() / 3600.0);
    min = (int)(Math.abs(gridShift.getShiftedLatSeconds()) / 60.0);
    sec = Math.abs(gridShift.getShiftedLatSeconds()) - (min * 60.0);
    min = min % 60;
    toDMSDegLatText.setValue(new Integer(deg));
    toDMSMinLatText.setValue(new Integer(min));
    toDMSSecLatText.setValue(new Double(sec));
  }

  public void setFromLonValues() {
    fromDegreeLonText.setText(degreeDecimalFormat.format(gridShift.getLonPositiveEastDegrees()));
    fromSecondLonText.setText(secondDecimalFormat.format(gridShift.getLonPositiveWestSeconds()));
    int deg = (int)(gridShift.getLonPositiveWestSeconds() / -3600.0);
    int min = (int)(Math.abs(gridShift.getLonPositiveWestSeconds()) / 60.0);
    double sec = Math.abs(gridShift.getLonPositiveWestSeconds()) - (min * 60.0);
    min = min % 60;
    fromDMSDegLonText.setText(degMinDecimalFormat.format(deg));
    fromDMSMinLonText.setText(degMinDecimalFormat.format(min));
    fromDMSSecLonText.setText(secondDecimalFormat.format(sec));
  }

  public void setFromLatValues() {
    fromDegreeLatText.setText(degreeDecimalFormat.format(gridShift.getLatDegrees()));
    fromSecondLatText.setText(secondDecimalFormat.format(gridShift.getLatSeconds()));
    int deg = (int)(gridShift.getLatSeconds() / 3600.0);
    int min = (int)(Math.abs(gridShift.getLatSeconds()) / 60.0);
    double sec = Math.abs(gridShift.getLatSeconds()) - (min * 60.0);
    min = min % 60;
    fromDMSDegLatText.setText(degMinDecimalFormat.format(deg));
    fromDMSMinLatText.setText(degMinDecimalFormat.format(min));
    fromDMSSecLatText.setText(secondDecimalFormat.format(sec));
  }

  class FileAction extends AbstractAction {
    private static final long serialVersionUID = 5887590788170519247L;
    public FileAction() {
      super("File");
    }
    public void actionPerformed(ActionEvent e) {
      int returnVal = fileChooser.showOpenDialog(GuiPanel.this);
      if(returnVal == JFileChooser.APPROVE_OPTION) {
        gui.unloadGridShiftFile();
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        gridShiftFileName.setText(filePath);
        boolean loaded = gui.loadGridShiftFile(filePath);
        if (loaded) {
          gridShiftErrorLabel.setText(" ");
          forwardButton.setText(gui.getForwardString());
          forwardButton.setEnabled(true);
          reverseButton.setText(gui.getReverseString());
          reverseButton.setEnabled(true);
        } else {
          gridShiftErrorLabel.setText(NOT_GSF);
          forwardButton.setText(Gui.NOT_LOADED);
          forwardButton.setEnabled(false);
          reverseButton.setText(Gui.NOT_LOADED);
          reverseButton.setEnabled(false);
        }
      }
    }
  }

  class FromAction extends AbstractAction {
    private static final long serialVersionUID = 3316898057199807664L;
    public FromAction() {
      super("From");
    }
    public void actionPerformed(ActionEvent e) {
      fromCardLayout.show(fromCardPanel, fromCombo.getSelectedItem().toString());
    }
  }

  class ToAction extends AbstractAction {
    private static final long serialVersionUID = 7206333928845895488L;
    public ToAction() {
      super("To");
    }
    public void actionPerformed(ActionEvent e) {
      toCardLayout.show(toCardPanel, toCombo.getSelectedItem().toString());
    }
  }

  class ForwardAction extends AbstractAction {
    private static final long serialVersionUID = 2912766666476978861L;
    public ForwardAction() {
      super("Forward");
    }
    public void actionPerformed(ActionEvent e) {
      try {
        if (gui.gridShiftForward(gridShift)) {
          setToValues();
          message.setText(gridShift.getSubGridName());
        } else {
          clearToValues();
          message.setText("Coordinate not in Grid Shift File");
        }
      } catch (IOException ioe) {
        clearToValues();
        message.setText("IO Exception : " + ioe.getMessage());
      }
    }
  }

  class ReverseAction extends AbstractAction {
    private static final long serialVersionUID = 3744835425419329471L;
    public ReverseAction() {
      super("Reverse");
    }
    public void actionPerformed(ActionEvent e) {
      try {
        if (gui.gridShiftReverse(gridShift)) {
          setToValues();
          message.setText(gridShift.getSubGridName());
        } else {
          clearToValues();
          message.setText("Coordinate not in Grid Shift File");
        }
      } catch (IOException ioe) {
        clearToValues();
        message.setText("IO Exception : " + ioe.getMessage());
      }
    }
  }

  class FileVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      String filePath = ((JTextField)comp).getText();
      if (filePath.equals("")) {
        gui.unloadGridShiftFile();
        gridShiftErrorLabel.setText(" ");
        return true;
      }
      boolean loaded = gui.loadGridShiftFile(filePath);
      if (loaded) {
        gridShiftErrorLabel.setText(" ");
        forwardButton.setText(gui.getForwardString());
        forwardButton.setEnabled(true);
        reverseButton.setText(gui.getReverseString());
        reverseButton.setEnabled(true);
      } else {
        gridShiftErrorLabel.setText(NOT_GSF);
        forwardButton.setText(Gui.NOT_LOADED);
        forwardButton.setEnabled(false);
        reverseButton.setText(Gui.NOT_LOADED);
        reverseButton.setEnabled(false);
      }
      return loaded;
    }
  }

  class FromLonDegreeVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number n = degreeDecimalFormat.parse(tf.getText());
        gridShift.setLonPositiveEastDegrees(n.doubleValue());
        setFromLonValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLatDegreeVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number n = degreeDecimalFormat.parse(tf.getText());
        gridShift.setLatDegrees(n.doubleValue());
        setFromLatValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLonSecondVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number n = secondDecimalFormat.parse(tf.getText());
        gridShift.setLonPositiveWestSeconds(n.doubleValue());
        setFromLonValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLatSecondVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number n = secondDecimalFormat.parse(tf.getText());
        gridShift.setLatSeconds(n.doubleValue());
        setFromLatValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLonDMSDegVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number deg = degMinDecimalFormat.parse(tf.getText());
        double sign = (deg.doubleValue() < 0.0) ? 1.0 : -1.0;
        Number min = ZERO;
        if (fromDMSMinLonText.getText().length() > 0) {
          min = degMinDecimalFormat.parse(fromDMSMinLonText.getText());
        }
        Number sec = ZERO;
        if (fromDMSSecLonText.getText().length() > 0) {
          sec = secondDecimalFormat.parse(fromDMSSecLonText.getText());
        }
        gridShift.setLonPositiveWestSeconds(
          (
            (Math.abs(deg.doubleValue()) * 3600.0) +
            (Math.abs(min.doubleValue()) * 60.0) +
            Math.abs(sec.doubleValue())
          ) * sign
        );
        setFromLonValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLatDMSDegVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number deg = degMinDecimalFormat.parse(tf.getText());
        double sign = (deg.doubleValue() < 0.0) ? -1.0 : 1.0;
        Number min = ZERO;
        if (fromDMSMinLatText.getText().length() > 0) {
          min = degMinDecimalFormat.parse(fromDMSMinLatText.getText());
        }
        Number sec = ZERO;
        if (fromDMSSecLatText.getText().length() > 0) {
          sec = secondDecimalFormat.parse(fromDMSSecLatText.getText());
        }
        gridShift.setLatSeconds(
          (
            (Math.abs(deg.doubleValue()) * 3600.0) +
            (Math.abs(min.doubleValue()) * 60.0) +
            Math.abs(sec.doubleValue())
          ) * sign
        );
        setFromLatValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLonDMSMinVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number deg = ZERO;
        if (fromDMSDegLonText.getText().length() > 0) {
          deg = degMinDecimalFormat.parse(fromDMSDegLonText.getText());
        }
        double sign = (deg.doubleValue() < 0.0) ? 1.0 : -1.0;
        Number min = degMinDecimalFormat.parse(tf.getText());
        Number sec = ZERO;
        if (fromDMSSecLonText.getText().length() > 0) {
          sec = secondDecimalFormat.parse(fromDMSSecLonText.getText());
        }
        gridShift.setLonPositiveWestSeconds(
          (
            (Math.abs(deg.doubleValue()) * 3600.0) +
            (Math.abs(min.doubleValue()) * 60.0) +
            Math.abs(sec.doubleValue())
          ) * sign
        );
        setFromLonValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLatDMSMinVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number deg = ZERO;
        if (fromDMSDegLatText.getText().length() > 0) {
          deg = degMinDecimalFormat.parse(fromDMSDegLatText.getText());
        }
        double sign = (deg.doubleValue() < 0.0) ? -1.0 : 1.0;
        Number min = degMinDecimalFormat.parse(tf.getText());
        Number sec = ZERO;
        if (fromDMSSecLatText.getText().length() > 0) {
          sec = secondDecimalFormat.parse(fromDMSSecLatText.getText());
        }
        gridShift.setLatSeconds(
          (
            (Math.abs(deg.doubleValue()) * 3600.0) +
            (Math.abs(min.doubleValue()) * 60.0) +
            Math.abs(sec.doubleValue())
          ) * sign
        );
        setFromLatValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLonDMSSecVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
      try {
        Number deg = ZERO;
        if (fromDMSDegLonText.getText().length() > 0) {
          deg = degMinDecimalFormat.parse(fromDMSDegLonText.getText());
        }
        double sign = (deg.doubleValue() < 0.0) ? 1.0 : -1.0;
        Number min = ZERO;
        if (fromDMSMinLonText.getText().length() > 0) {
          min = degMinDecimalFormat.parse(fromDMSMinLonText.getText());
        }
        Number sec = secondDecimalFormat.parse(tf.getText());
        gridShift.setLonPositiveWestSeconds(
          (
            (Math.abs(deg.doubleValue()) * 3600.0) +
            (Math.abs(min.doubleValue()) * 60.0) +
            Math.abs(sec.doubleValue())
          ) * sign
        );
        setFromLonValues();
        ok = true;
      } catch (ParseException pe) {
        ok = false;
      }
      return ok;
    }
  }

  class FromLatDMSSecVerifier extends InputVerifier {
    public boolean verify(JComponent comp) {
      JTextField tf = (JTextField)comp;
      boolean ok = false;
        try {
          Number deg = ZERO;
          if (fromDMSDegLatText.getText().length() > 0) {
            deg = degMinDecimalFormat.parse(fromDMSDegLatText.getText());
          }
          double sign = (deg.doubleValue() < 0.0) ? -1.0 : 1.0;
          Number min = ZERO;
          if (fromDMSMinLatText.getText().length() > 0) {
            min = degMinDecimalFormat.parse(fromDMSMinLatText.getText());
          }
          Number sec = secondDecimalFormat.parse(tf.getText());
          gridShift.setLatSeconds(
            (
              (Math.abs(deg.doubleValue()) * 3600.0) +
              (Math.abs(min.doubleValue()) * 60.0) +
              Math.abs(sec.doubleValue())
            ) * sign
          );
          setFromLatValues();
          ok = true;
        } catch (ParseException pe) {
          ok = false;
        }
      return ok;
    }
  }

  class EntryFocus implements FocusListener {
    public void focusGained(FocusEvent e) {
      if (!e.isTemporary()) {
        JTextComponent t = (JTextComponent)e.getComponent();
        // seems to be a bug in JFormattedTextField that stops this working
        t.selectAll();
        clearToValues();
      }
    }
    public void focusLost(FocusEvent e) {}
  }
}
