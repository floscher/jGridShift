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
package au.com.objectix.jgridshift;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Models the NTv2 Sub Grid within a Grid Shift File
 *
 * @author Peter Yuill
 */
public class SubGrid implements Cloneable, Serializable {

  private static final long serialVersionUID = 1887278896684381292L;
  private static final int REC_SIZE = 16;

  private String subGridName;
  private String parentSubGridName;
  private String created;
  private String updated;
  private double minLat;
  private double maxLat;
  private double minLon;
  private double maxLon;
  private double latInterval;
  private double lonInterval;
  private int nodeCount;

  private int lonColumnCount;
  private int latRowCount;
  private float[] latShift;
  private float[] lonShift;
  private float[] latAccuracy;
  private float[] lonAccuracy;

  private RandomAccessFile raf;
  private long subGridOffset;
  boolean bigEndian;
  private SubGrid[] subGrid;

  /**
   * Construct a Sub Grid from an InputStream, loading the node data into
   * arrays in this object.
   *
   * @param in GridShiftFile InputStream
   * @param bigEndian is the file bigEndian?
   * @param loadAccuracy is the node Accuracy data to be loaded?
   * @throws IOException
   */
  public SubGrid(InputStream in, boolean bigEndian, boolean loadAccuracy) throws IOException {
    byte[] b8 = new byte[8];
    byte[] b4 = new byte[4];
    in.read(b8);
    in.read(b8);
    subGridName = new String(b8, StandardCharsets.UTF_8).trim();
    in.read(b8);
    in.read(b8);
    parentSubGridName = new String(b8, StandardCharsets.UTF_8).trim();
    in.read(b8);
    in.read(b8);
    created = new String(b8, StandardCharsets.UTF_8);
    in.read(b8);
    in.read(b8);
    updated = new String(b8, StandardCharsets.UTF_8);
    in.read(b8);
    in.read(b8);
    minLat = Util.getDouble(b8, bigEndian);
    in.read(b8);
    in.read(b8);
    maxLat = Util.getDouble(b8, bigEndian);
    in.read(b8);
    in.read(b8);
    minLon = Util.getDouble(b8, bigEndian);
    in.read(b8);
    in.read(b8);
    maxLon = Util.getDouble(b8, bigEndian);
    in.read(b8);
    in.read(b8);
    latInterval = Util.getDouble(b8, bigEndian);
    in.read(b8);
    in.read(b8);
    lonInterval = Util.getDouble(b8, bigEndian);
    lonColumnCount = 1 + (int)((maxLon - minLon) / lonInterval);
    latRowCount = 1 + (int)((maxLat - minLat) / latInterval);
    in.read(b8);
    in.read(b8);
    nodeCount = Util.getInt(b8, bigEndian);
    if (nodeCount != lonColumnCount * latRowCount) {
      throw new IllegalStateException("SubGrid " + subGridName + " has inconsistent grid dimesions");
    }
    latShift = new float[nodeCount];
    lonShift = new float[nodeCount];
    if (loadAccuracy) {
      latAccuracy = new float[nodeCount];
      lonAccuracy = new float[nodeCount];
    }

    for (int i = 0; i < nodeCount; i++) {
      latShift[i] = Util.getFloat(readBytes(in, 4), bigEndian);
      lonShift[i] = Util.getFloat(readBytes(in, 4), bigEndian);
      b4 = readBytes(in, 4);
      if (loadAccuracy) {
        latAccuracy[i] = Util.getFloat(b4, bigEndian);
      }
      b4 = readBytes(in, 4);
      if (loadAccuracy) {
        lonAccuracy[i] = Util.getFloat(b4, bigEndian);
      }
    }
  }
  /**
   * Helper method for the constructor {@link #SubGrid(InputStream, boolean, boolean)}.
   * It reads a number of bytes from an {@link InputStream} one byte after another.
   *
   * Read the grid file byte after byte. This is a workaround about a bug in
   * certain VM which are not able to read byte blocks when the resource file is
   * in a .jar file (Pieren)
   * @param in the {@link InputStream} to read from
   * @param numBytes the number of bytes to read
   * @return the resulting byte-array
   * @throws IOException
   */
  private byte[] readBytes(InputStream in, int numBytes) throws IOException {
    int readBytes = 0;
    byte[] result = new byte[numBytes];
    while (readBytes <= numBytes-1) {
      readBytes += in.read(result, readBytes, 1);
    }
    return result;
  }

  /**
   * Construct a Sub Grid from a RandomAccessFile. Only the headers
   * are loaded into this object, the node data is accessed directly
   * from the RandomAccessFile.
   *
   * @param in GridShiftFile RandomAccessFile
   * @param bigEndian is the file bigEndian?
   * @throws Exception
   */
  public SubGrid(RandomAccessFile raf, long subGridOffset, boolean bigEndian) throws IOException {
    this.raf = raf;
    this.subGridOffset = subGridOffset;
    this.bigEndian = bigEndian;
    raf.seek(subGridOffset);
    byte[] b8 = new byte[8];
    raf.read(b8);
    raf.read(b8);
    subGridName = new String(b8).trim();
    raf.read(b8);
    raf.read(b8);
    parentSubGridName = new String(b8).trim();
    raf.read(b8);
    raf.read(b8);
    created = new String(b8);
    raf.read(b8);
    raf.read(b8);
    updated = new String(b8);
    raf.read(b8);
    raf.read(b8);
    minLat = Util.getDouble(b8, bigEndian);
    raf.read(b8);
    raf.read(b8);
    maxLat = Util.getDouble(b8, bigEndian);
    raf.read(b8);
    raf.read(b8);
    minLon = Util.getDouble(b8, bigEndian);
    raf.read(b8);
    raf.read(b8);
    maxLon = Util.getDouble(b8, bigEndian);
    raf.read(b8);
    raf.read(b8);
    latInterval = Util.getDouble(b8, bigEndian);
    raf.read(b8);
    raf.read(b8);
    lonInterval = Util.getDouble(b8, bigEndian);
    lonColumnCount = 1 + (int)((maxLon - minLon) / lonInterval);
    latRowCount = 1 + (int)((maxLat - minLat) / latInterval);
    raf.read(b8);
    raf.read(b8);
    nodeCount = Util.getInt(b8, bigEndian);
    if (nodeCount != lonColumnCount * latRowCount) {
      throw new IllegalStateException("SubGrid " + subGridName + " has inconsistent grid dimesions");
    }
  }

  /**
   * Tests if a specified coordinate is within this Sub Grid
   * or one of its Sub Grids. If the coordinate is outside
   * this Sub Grid, null is returned. If the coordinate is
   * within this Sub Grid, but not within any of its Sub Grids,
   * this Sub Grid is returned. If the coordinate is within
   * one of this Sub Grid's Sub Grids, the method is called
   * recursively on the child Sub Grid.
   *
   * @param lon Longitude in Positive West Seconds
   * @param lat Latitude in Seconds
   * @return the Sub Grid containing the Coordinate or null
   */
  public SubGrid getSubGridForCoord(double lon, double lat) {
    if (isCoordWithin(lon, lat)) {
      if (subGrid == null) {
        return this;
      } else {
        for (SubGrid s : subGrid) {
          if (s.isCoordWithin(lon, lat)) {
            return s.getSubGridForCoord(lon, lat);
          }
        }
        return this;
      }
    } else {
      return null;
    }
  }

  /**
   * Tests if a specified coordinate is within this Sub Grid.
   * A coordinate on either outer edge (maximum Latitude or
   * maximum Longitude) is deemed to be outside the grid.
   *
   * @param lon Longitude in Positive West Seconds
   * @param lat Latitude in Seconds
   * @return true or false
   */
  private boolean isCoordWithin(double lon, double lat) {
    return (lon >= minLon) && (lon < maxLon) && (lat >= minLat) && (lat < maxLat);
  }

  /**
   * Bi-Linear interpolation of four nearest node values as described in
   * 'GDAit Software Architecture Manual' produced by the <a
   * href='http://www.sli.unimelb.edu.au/gda94'>Geomatics
   * Department of the University of Melbourne</a>
   * @param a value at the A node
   * @param b value at the B node
   * @param c value at the C node
   * @param d value at the D node
   * @param X Longitude factor
   * @param Y Latitude factor
   * @return interpolated value
   */
  private final double interpolate(float a, float b, float c, float d, double X, double Y) {
    return (double)a + (((double)b - (double)a) * X) + (((double)c - (double)a) * Y) +
      (((double)a + (double)d - (double)b - (double)c) * X * Y);
  }

  /**
   * Interpolate shift and accuracy values for a coordinate in the 'from' datum
   * of the GridShiftFile. The algorithm is described in
   * 'GDAit Software Architecture Manual' produced by the <a
   * href='http://www.sli.unimelb.edu.au/gda94'>Geomatics
   * Department of the University of Melbourne</a>
   * <p>This method is thread safe for both memory based and file based node data.
   * @param gs GridShift object containing the coordinate to shift and the shift values
   * @return the GridShift object supplied, with values updated.
   * @throws IOException
   */
  public GridShift interpolateGridShift(GridShift gs) throws IOException {
    int lonIndex = (int)((gs.getLonPositiveWestSeconds() - minLon) / lonInterval);
    int latIndex = (int)((gs.getLatSeconds() - minLat) / latInterval);

    double X = (gs.getLonPositiveWestSeconds() - (minLon + (lonInterval * lonIndex))) / lonInterval;
    double Y = (gs.getLatSeconds() - (minLat + (latInterval * latIndex))) / latInterval;

    // Find the nodes at the four corners of the cell

    int indexA = lonIndex + (latIndex * lonColumnCount);
    int indexB = indexA + 1;
    int indexC = indexA + lonColumnCount;
    int indexD = indexC + 1;

    if (raf == null) {
      gs.setLonShiftPositiveWestSeconds(interpolate(lonShift[indexA], lonShift[indexB], lonShift[indexC], lonShift[indexD], X, Y));

      gs.setLatShiftSeconds(interpolate(latShift[indexA], latShift[indexB], latShift[indexC], latShift[indexD], X, Y));

      if (lonAccuracy == null) {
        gs.setLonAccuracyAvailable(false);
      } else {
        gs.setLonAccuracyAvailable(true);
        gs.setLonAccuracySeconds(interpolate(lonAccuracy[indexA], lonAccuracy[indexB], lonAccuracy[indexC], lonAccuracy[indexD], X, Y));
      }

      if (latAccuracy == null) {
        gs.setLatAccuracyAvailable(false);
      } else {
        gs.setLatAccuracyAvailable(true);
        gs.setLatAccuracySeconds(interpolate(latAccuracy[indexA], latAccuracy[indexB], latAccuracy[indexC], latAccuracy[indexD], X, Y));
      }
    } else {
      synchronized(raf) {
        byte[] b4 = new byte[4];
        long nodeOffset = subGridOffset + (11 * REC_SIZE) + (indexA * REC_SIZE);
        raf.seek(nodeOffset);
        raf.read(b4);
        float latShiftA = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonShiftA = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float latAccuracyA = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonAccuracyA = Util.getFloat(b4, bigEndian);

        nodeOffset = subGridOffset + (11 * REC_SIZE) + (indexB * REC_SIZE);
        raf.seek(nodeOffset);
        raf.read(b4);
        float latShiftB = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonShiftB = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float latAccuracyB = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonAccuracyB = Util.getFloat(b4, bigEndian);

        nodeOffset = subGridOffset + (11 * REC_SIZE) + (indexC * REC_SIZE);
        raf.seek(nodeOffset);
        raf.read(b4);
        float latShiftC = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonShiftC = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float latAccuracyC = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonAccuracyC = Util.getFloat(b4, bigEndian);

        nodeOffset = subGridOffset + (11 * REC_SIZE) + (indexD * REC_SIZE);
        raf.seek(nodeOffset);
        raf.read(b4);
        float latShiftD = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonShiftD = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float latAccuracyD = Util.getFloat(b4, bigEndian);
        raf.read(b4);
        float lonAccuracyD = Util.getFloat(b4, bigEndian);

        gs.setLonShiftPositiveWestSeconds(interpolate(lonShiftA, lonShiftB, lonShiftC, lonShiftD, X, Y));

        gs.setLatShiftSeconds(interpolate(latShiftA, latShiftB, latShiftC, latShiftD, X, Y));

        gs.setLonAccuracyAvailable(true);
        gs.setLonAccuracySeconds(interpolate(lonAccuracyA, lonAccuracyB, lonAccuracyC, lonAccuracyD, X, Y));

        gs.setLatAccuracyAvailable(true);
        gs.setLatAccuracySeconds(interpolate(latAccuracyA, latAccuracyB, latAccuracyC, latAccuracyD, X, Y));
      }
    }
    return gs;
  }

  public String getParentSubGridName() {
    return parentSubGridName;
  }

  public String getSubGridName() {
    return subGridName;
  }

  public int getNodeCount() {
    return nodeCount;
  }

  public int getSubGridCount() {
    return (subGrid == null) ? 0 : subGrid.length;
  }

  public SubGrid getSubGrid(int index) {
    return (subGrid == null) ? null : subGrid[index];
  }

  /**
   * Set an array of Sub Grids of this sub grid
   * @param subGrid
   */
  public void setSubGridArray(SubGrid[] subGrid) {
    this.subGrid = subGrid == null ? null : Arrays.copyOf(subGrid, subGrid.length);
  }

  @Override
  public String toString() {
    return subGridName;
  }

  /**
   * Returns textual details about the sub grid.
   * @return textual details about the sub grid
   */
  public String getDetails() {
    StringBuilder buf = new StringBuilder("Sub Grid : ");
    buf.append(subGridName);
    buf.append("\nParent   : ");
    buf.append(parentSubGridName);
    buf.append("\nCreated  : ");
    buf.append(created);
    buf.append("\nUpdated  : ");
    buf.append(updated);
    buf.append("\nMin Lat  : ");
    buf.append(minLat);
    buf.append("\nMax Lat  : ");
    buf.append(maxLat);
    buf.append("\nMin Lon  : ");
    buf.append(minLon);
    buf.append("\nMax Lon  : ");
    buf.append(maxLon);
    buf.append("\nLat Intvl: ");
    buf.append(latInterval);
    buf.append("\nLon Intvl: ");
    buf.append(lonInterval);
    buf.append("\nNode Cnt : ");
    buf.append(nodeCount);
    return buf.toString();
  }

  /**
   * Make a deep clone of this Sub Grid
   */
  @Override
  public Object clone() {
    SubGrid clone = null;
    try {
      clone = (SubGrid)super.clone();
      // Do a deep clone of the sub grids
      if (subGrid != null) {
        clone.subGrid = new SubGrid[subGrid.length];
        for (int i = 0; i < subGrid.length; i++) {
          clone.subGrid[i] = (SubGrid)subGrid[i].clone();
        }
      }
    } catch (CloneNotSupportedException cnse) {
    }
    return clone;
  }
  /**
   * Get maximum latitude value
   * @return maximum latitude
   */
  public double getMaxLat() {
    return maxLat;
  }

  /**
   * Get maximum longitude value
   * @return maximum longitude
   */
  public double getMaxLon() {
    return maxLon;
  }

  /**
   * Get minimum latitude value
   * @return minimum latitude
   */
  public double getMinLat() {
    return minLat;
  }

  /**
   * Get minimum longitude value
   * @return minimum longitude
   */
  public double getMinLon() {
    return minLon;
  }
}
