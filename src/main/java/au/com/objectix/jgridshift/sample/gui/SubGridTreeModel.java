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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import au.com.objectix.jgridshift.SubGrid;

/**
 * A special purpose TreeModel for a SubGrid tree.
 *
 * @author Peter Yuill
 */
public class SubGridTreeModel implements TreeModel {

  private static final int MIN_LAT = 0;
  private static final int MAX_LAT = 1;
  private static final int MIN_LON = 2;
  private static final int MAX_LON = 3;

  private SubGrid[] subGridTree;
  private RootNode root;

  /**
   * No content constructor
   */
  public SubGridTreeModel() {
    root = new RootNode(0);
  }

  /**
   * Construct a model for an array of top level SubGrids with
   * child grids set.
   */
  public SubGridTreeModel(SubGrid[] subGridTree) {
    this.subGridTree = subGridTree;
    root = new RootNode(subGridTree.length);
  }

  /**
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  public Object getRoot() {
    return root;
  }

  /**
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  public Object getChild(Object parent, int index) {
    if (parent == root) {
      return subGridTree[index];
    } else {
      SubGrid parentSubGrid = (SubGrid)parent;
      if (index < 4) {
        switch (index) {
          case MIN_LAT :
            return new DimensionNode(MIN_LAT, parentSubGrid.getMinLat());
          case MAX_LAT :
            return new DimensionNode(MAX_LAT, parentSubGrid.getMaxLat());
          case MIN_LON :
            return new DimensionNode(MIN_LON, parentSubGrid.getMinLon());
          case MAX_LON :
            return new DimensionNode(MAX_LON, parentSubGrid.getMaxLon());
          default :
            throw new IllegalArgumentException("");
        }
      } else {
        return parentSubGrid.getSubGrid(index - 4);
      }
    }
  }

  /**
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount(Object parent) {
    if (parent == root) {
      return root.getNodeCount();
    } else {
      return ((SubGrid)parent).getSubGridCount() + 4;
    }
  }

  /**
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf(Object node) {
    boolean leaf = (node instanceof DimensionNode);
    if (!leaf) {
      if (node instanceof RootNode) {
        RootNode root = (RootNode)node;
        leaf = (root.getNodeCount() == 0);
      }
    }
    return leaf;
  }

  /**
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  public void valueForPathChanged(TreePath path, Object newValue) {}

  /**
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (parent == root) {
      for (int i = 0; i < subGridTree.length; i++) {
        if (subGridTree[i] == child) {
          return i;
        }
      }
      throw new IllegalArgumentException( child + " not a child of this parent " + parent);
    } else if (child instanceof DimensionNode){
      return ((DimensionNode)child).getType();
    } else {
      SubGrid parentSubGrid = (SubGrid)parent;
      for (int i = 0; i < parentSubGrid.getSubGridCount(); i++) {
        if (parentSubGrid.getSubGrid(i) == child) {
          return i;
        }
      }
      throw new IllegalArgumentException( child + " not a child of this parent " + parent);
    }
  }

  /**
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void addTreeModelListener(TreeModelListener l) {}

  /**
   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void removeTreeModelListener(TreeModelListener l) {}

  public static class DimensionNode {
    int type;
    double value;
    public DimensionNode(int type, double value) {
      this.type = type;
      this.value = value;
    }
    public String toString() {
      StringBuffer buf = new StringBuffer();
      switch (type) {
        case MIN_LAT :
          buf.append("Min Lat: ");
          break;
        case MAX_LAT :
          buf.append("Max Lat: ");
          break;
        case MIN_LON :
          buf.append("Min Lon: ");
          break;
        case MAX_LON :
          buf.append("Max Lon: ");
          break;
      }
      buf.append(value);
      return buf.toString();
    }
    public int getType() {
      return type;
    }
  }

  public static class RootNode {
    private int nodeCount;
    public RootNode(int nodeCount) {
      this.nodeCount = nodeCount;
    }
    public int getNodeCount() {
      return nodeCount;
    }
    public String toString() {
      if (nodeCount == 0) {
        return "Not Loaded";
      } else {
        return "";
      }
    }
  }
}
