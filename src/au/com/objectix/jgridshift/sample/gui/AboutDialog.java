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

import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Container;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Font;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Peter Yuill
 */
public class AboutDialog extends JDialog {
    
    public AboutDialog(Frame owner) {
        super(owner, true);
        ImageIcon icon = new ImageIcon(this.getClass().getResource("jgridshift.gif"));
        JLabel iconLabel = new JLabel(icon);
        Container content = this.getContentPane();
        GridBagLayout gridbag = new GridBagLayout();
        content.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(iconLabel, c);
        content.add(iconLabel);
        JLabel filler1 = new JLabel(" ");
        gridbag.setConstraints(filler1, c);
        content.add(filler1);
        JLabel web = new JLabel("http://jgridshift.sourceforge.net");
        gridbag.setConstraints(web, c);
        content.add(web);
        JLabel filler2 = new JLabel(" ");
        gridbag.setConstraints(filler2, c);
        content.add(filler2);
        JLabel copyright = new JLabel("Copyright (c) 2003 Objectix Pty Ltd  All rights reserved.");
        gridbag.setConstraints(copyright, c);
        content.add(copyright);
        JLabel filler3 = new JLabel(" ");
        gridbag.setConstraints(filler3, c);
        content.add(filler3);
        JLabel license = new JLabel("This software is licensed under the terms of the GNU Lesser General Public License");
        gridbag.setConstraints(license, c);
        content.add(license);
        StringBuffer buf = new StringBuffer();
        InputStream in = this.getClass().getResourceAsStream("lgpl.txt");
        int ch = -1;
        try {
            while((ch = in.read()) >= 0) {
                buf.append((char)ch);
            }
        } catch (IOException ioe) {}
        JTextArea licText = new JTextArea(buf.toString(), 504, 80);
        licText.setFont(new Font("Monospaced",Font.PLAIN, 10));
        JScrollPane scroll = new JScrollPane(licText);
        scroll.setPreferredSize(new Dimension(500, 150));
        c.fill = GridBagConstraints.HORIZONTAL;
        gridbag.setConstraints(scroll, c);
        content.add(scroll);
    }
}
