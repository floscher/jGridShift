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

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.AbstractAction;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import au.com.objectix.jgridshift.NTv2GridShift;
import au.com.objectix.jgridshift.NTv2GridShiftFile;

/**
 * @author peter
 */
public class Gui extends JFrame {

  public static final String TITLE = "jGridShift";
  public static final String NOT_LOADED = "Not Loaded";
  private NTv2GridShiftFile gridShiftFile = new NTv2GridShiftFile();
  private JTree tree;
  private File userPropertiesFile;
  private Properties userProperties = new Properties();

  public Gui() throws Exception {
    super(TITLE);
    JMenuBar menuBar = new JMenuBar();
    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(new AboutAction());
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);
    tree = new JTree(new SubGridTreeModel());
    String userHome = System.getProperty("user.home");
    userPropertiesFile = new File(userHome, "jgridshift.properties");
    if (!userPropertiesFile.exists()) {
      userPropertiesFile.createNewFile();
    }
    userProperties.load(new FileInputStream(userPropertiesFile));
    String gridShiftFileName = (String)userProperties.get("gridShiftFile");
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setPreferredSize(new Dimension(100, 150));
    GridBagLayout gridbag = new GridBagLayout();
    GuiPanel guiPanel = new GuiPanel(gridShiftFileName, gridbag, this);
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, guiPanel, scrollPane);
    this.getContentPane().add(splitPane, BorderLayout.CENTER);
  }

  public boolean loadGridShiftFile(String filePath) {
    boolean loaded = false;
    try {
      gridShiftFile.loadGridShiftFile(new RandomAccessFile(filePath, "r"));
      loaded = true;
      tree.setModel(new SubGridTreeModel(gridShiftFile.getSubGridTree()));
      userProperties.setProperty("gridShiftFile", filePath);
      try {
        userProperties.store(new FileOutputStream(userPropertiesFile), null);
      } catch (IOException ioe) {}
    } catch (Exception ex) {
      tree.setModel(new SubGridTreeModel());
    }
    return loaded;
  }

  public void unloadGridShiftFile() {
    try {
      gridShiftFile.unload();
    } catch (IOException ioe) {}
  }

  public boolean gridShiftForward(NTv2GridShift gs) throws IOException {
    return gridShiftFile.gridShiftForward(gs);
  }

  public boolean gridShiftReverse(NTv2GridShift gs) throws IOException {
    return gridShiftFile.gridShiftReverse(gs);
  }

  public String getForwardString() {
    if (gridShiftFile.isLoaded()) {
      return gridShiftFile.getFromEllipsoid() + " -> " + gridShiftFile.getToEllipsoid();
    } else {
      return NOT_LOADED;
    }
  }

  public String getReverseString() {
    if (gridShiftFile.isLoaded()) {
      return gridShiftFile.getToEllipsoid() + " -> " + gridShiftFile.getFromEllipsoid();
    } else {
      return NOT_LOADED;
    }
  }

  public static void main(String[] args) {
    try {
      JFrame frame = new Gui();
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  class AboutAction extends AbstractAction {
    public AboutAction() {
      super("About");
    }
    public void actionPerformed(ActionEvent e) {
      AboutDialog dialog = new AboutDialog(Gui.this);
      dialog.pack();
      dialog.show();
      dialog.dispose();
    }
  }
}
