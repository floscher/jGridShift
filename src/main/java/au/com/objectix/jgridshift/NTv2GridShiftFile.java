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
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Models the NTv2 format Grid Shift File and exposes methods to shift
 * coordinate values using the Sub Grids contained in the file.
 * <p>
 * The principal reference for the alogrithms used is the 'GDAit Software
 * Architecture Manual' produced by the <a
 * href='http://www.sli.unimelb.edu.au/gda94'>Geomatics Department of the
 * University of Melbourne</a>
 * </p><p>
 * This library reads binary NTv2 Grid Shift files in Big Endian (Canadian
 * standard) or Little Endian (Australian Standard) format. The older
 * 'Australian' binary format is not supported, only the official Canadian
 * format, which is now also used for the national Australian Grid.
 * </p><p>
 * Grid Shift files can be read as InputStreams or RandomAccessFiles. Loading an
 * InputStream places all the required node information (accuracy data is
 * optional) into heap based Java arrays. This is the highest perfomance option,
 * and is useful for large volume transformations. Non-file data sources (eg
 * using an SQL Blob) are also supported through InputStream. The
 * RandomAccessFile option has a much smaller memory footprint as only the Sub
 * Grid headers are stored in memory, but transformation is slower because the
 * file must be read a number of times for each transformation.
 * </p><p>
 * Coordinates may be shifted Forward (ie from and to the Datums specified in
 * the Grid Shift File header) or Reverse. The reverse transformation uses an
 * iterative approach to approximate the Grid Shift, as the precise
 * transformation is based on 'from' datum coordinates.
 * </p><p>
 * Coordinates may be specified either in Seconds using Positive West Longitude
 * (the original NTv2 arrangement) or in decimal Degrees using Positive East
 * Longitude.
 * </p>
 *
 * @author Peter Yuill
 * Modifified for JOSM :
 * - removed the RandomAccessFile mode (Pieren)
 */
public class NTv2GridShiftFile implements Serializable {

  private static final long serialVersionUID = 1L;
  private int overviewHeaderCount;
  private int subGridHeaderCount;
  private int subGridCount;
  private String shiftType;
  private String version;
  private String fromEllipsoid = "";
  private String toEllipsoid = "";
  private double fromSemiMajorAxis;
  private double fromSemiMinorAxis;
  private double toSemiMajorAxis;
  private double toSemiMinorAxis;

  private NTv2SubGrid[] topLevelSubGrid;
  private NTv2SubGrid lastSubGrid;

  /**
   * Load a Grid Shift File from an InputStream. The Grid Shift node data is
   * stored in Java arrays, which will occupy about the same memory as the
   * original file with accuracy data included, and about half that with
   * accuracy data excluded. The size of the Australian national file is 4.5MB,
   * and the Canadian national file is 13.5MB
   * <p>The InputStream is closed by this method.</p>
   *
   * @param in Grid Shift File InputStream
   * @param loadAccuracy is Accuracy data to be loaded as well as shift data?
   * @throws IOException
   */
  public void loadGridShiftFile(InputStream in, boolean loadAccuracy) throws IOException {
    byte[] b8 = new byte[8];
    boolean bigEndian = true;
    fromEllipsoid = "";
    toEllipsoid = "";
    topLevelSubGrid = null;
    NTv2Util.readBytes(in, b8);
    String overviewHeaderCountId = new String(b8, StandardCharsets.UTF_8);
    if (!"NUM_OREC".equals(overviewHeaderCountId)) {
      throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
    }
    NTv2Util.readBytes(in, b8);
    overviewHeaderCount = NTv2Util.getIntBE(b8, 0);
    if (overviewHeaderCount == 11) {
      bigEndian = true;
    } else {
      overviewHeaderCount = NTv2Util.getIntLE(b8, 0);
      if (overviewHeaderCount == 11) {
        bigEndian = false;
      } else {
        throw new IllegalArgumentException("Input file is not an NTv2 grid shift file");
      }
    }
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    subGridHeaderCount = NTv2Util.getInt(b8, bigEndian);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    subGridCount = NTv2Util.getInt(b8, bigEndian);
    NTv2SubGrid[] subGrid = new NTv2SubGrid[subGridCount];
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    shiftType = new String(b8, StandardCharsets.UTF_8);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    version = new String(b8, StandardCharsets.UTF_8);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    fromEllipsoid = new String(b8, StandardCharsets.UTF_8);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    toEllipsoid = new String(b8, StandardCharsets.UTF_8);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    fromSemiMajorAxis = NTv2Util.getDouble(b8, bigEndian);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    fromSemiMinorAxis = NTv2Util.getDouble(b8, bigEndian);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    toSemiMajorAxis = NTv2Util.getDouble(b8, bigEndian);
    NTv2Util.readBytes(in, b8);
    NTv2Util.readBytes(in, b8);
    toSemiMinorAxis = NTv2Util.getDouble(b8, bigEndian);

    for (int i = 0; i < subGridCount; i++) {
      subGrid[i] = new NTv2SubGrid(in, bigEndian, loadAccuracy);
    }
    topLevelSubGrid = createSubGridTree(subGrid);
    lastSubGrid = topLevelSubGrid[0];
  }

  /**
   * Create a tree of Sub Grids by adding each Sub Grid to its parent (where it
   * has one), and returning an array of the top level Sub Grids
   *
   * @param subGrid an array of all Sub Grids
   * @return an array of top level Sub Grids with lower level Sub Grids set.
   */
  private NTv2SubGrid[] createSubGridTree(NTv2SubGrid[] subGrid) {
    int topLevelCount = 0;
    Map<String, List<NTv2SubGrid>> subGridMap = new HashMap<>();
    for (int i = 0; i < subGrid.length; i++) {
      if ("NONE".equalsIgnoreCase(subGrid[i].getParentSubGridName())) {
        topLevelCount++;
      }
      subGridMap.put(subGrid[i].getSubGridName(), new ArrayList<NTv2SubGrid>());
    }
    NTv2SubGrid[] topLevelSubGrid = new NTv2SubGrid[topLevelCount];
    topLevelCount = 0;
    for (int i = 0; i < subGrid.length; i++) {
      if ("NONE".equalsIgnoreCase(subGrid[i].getParentSubGridName())) {
        topLevelSubGrid[topLevelCount++] = subGrid[i];
      } else {
        List<NTv2SubGrid> parent = subGridMap.get(subGrid[i].getParentSubGridName());
        parent.add(subGrid[i]);
      }
    }
    NTv2SubGrid[] nullArray = new NTv2SubGrid[0];
    for (int i = 0; i < subGrid.length; i++) {
      List<NTv2SubGrid> subSubGrids = subGridMap.get(subGrid[i].getSubGridName());
      if (!subSubGrids.isEmpty()) {
        NTv2SubGrid[] subGridArray = subSubGrids.toArray(nullArray);
        subGrid[i].setSubGridArray(subGridArray);
      }
    }
    return topLevelSubGrid;
  }

  /**
   * Shift a coordinate in the Forward direction of the Grid Shift File.
   *
   * @param gs A {@link NTv2GridShift} object containing the coordinate to shift
   * @return True if the coordinate is within a Sub Grid, false if not
   */
  public boolean gridShiftForward(NTv2GridShift gs) {
    // Try the last sub grid first, big chance the coord is still within it
    NTv2SubGrid subGrid = lastSubGrid.getSubGridForCoord(gs.getLonPositiveWestSeconds(), gs.getLatSeconds());
    if (subGrid == null) {
      subGrid = getSubGrid(gs.getLonPositiveWestSeconds(), gs.getLatSeconds());
    }
    if (subGrid == null) {
      return false;
    } else {
      subGrid.interpolateGridShift(gs);
      gs.setSubGridName(subGrid.getSubGridName());
      lastSubGrid = subGrid;
      return true;
    }
  }

  /**
   * Shift a coordinate in the Reverse direction of the Grid Shift File.
   *
   * @param gs A {@link NTv2GridShift} object containing the coordinate to shift
   * @return True if the coordinate is within a Sub Grid, false if not
   */
  public boolean gridShiftReverse(NTv2GridShift gs) {
    // set up the first estimate
    NTv2GridShift forwardGs = new NTv2GridShift();
    forwardGs.setLonPositiveWestSeconds(gs.getLonPositiveWestSeconds());
    forwardGs.setLatSeconds(gs.getLatSeconds());
    for (int i = 0; i < 4; i++) {
      if (!gridShiftForward(forwardGs)) {
        return false;
      }
      forwardGs.setLonPositiveWestSeconds(gs.getLonPositiveWestSeconds() - forwardGs.getLonShiftPositiveWestSeconds());
      forwardGs.setLatSeconds(gs.getLatSeconds() - forwardGs.getLatShiftSeconds());
    }
    gs.setLonShiftPositiveWestSeconds(-forwardGs.getLonShiftPositiveWestSeconds());
    gs.setLatShiftSeconds(-forwardGs.getLatShiftSeconds());
    gs.setLonAccuracyAvailable(forwardGs.isLonAccuracyAvailable());
    if (forwardGs.isLonAccuracyAvailable()) {
      gs.setLonAccuracySeconds(forwardGs.getLonAccuracySeconds());
    }
    gs.setLatAccuracyAvailable(forwardGs.isLatAccuracyAvailable());
    if (forwardGs.isLatAccuracyAvailable()) {
      gs.setLatAccuracySeconds(forwardGs.getLatAccuracySeconds());
    }
    return true;
  }

  /**
   * Find the finest {@link NTv2SubGrid} containing the coordinate, specified in Positive
   * West Seconds
   *
   * @param lon Longitude in Positive West Seconds
   * @param lat Latitude in Seconds
   * @return The NTv2SubGrid found or null
   */
  private NTv2SubGrid getSubGrid(double lon, double lat) {
    NTv2SubGrid sub = null;
    for (int i = 0; i < topLevelSubGrid.length; i++) {
      sub = topLevelSubGrid[i].getSubGridForCoord(lon, lat);
      if (sub != null) {
        break;
      }
    }
    return sub;
  }

  public boolean isLoaded() {
    return topLevelSubGrid != null;
  }

  public void unload() {
    topLevelSubGrid = null;
  }

  @Override
  public String toString() {
    return new StringBuilder("Headers  : ")
      .append(overviewHeaderCount)
      .append("\nSub Hdrs : ")
      .append(subGridHeaderCount)
      .append("\nSub Grids: ")
      .append(subGridCount)
      .append("\nType     : ")
      .append(shiftType)
      .append("\nVersion  : ")
      .append(version)
      .append("\nFr Ellpsd: ")
      .append(fromEllipsoid)
      .append("\nTo Ellpsd: ")
      .append(toEllipsoid)
      .append("\nFr Maj Ax: ")
      .append(fromSemiMajorAxis)
      .append("\nFr Min Ax: ")
      .append(fromSemiMinorAxis)
      .append("\nTo Maj Ax: ")
      .append(toSemiMajorAxis)
      .append("\nTo Min Ax: ")
      .append(toSemiMinorAxis)
      .toString();
  }

  /**
   * Get a copy of the {@link NTv2SubGrid} tree for this file.
   *
   * @return a deep clone of the current NTv2SubGrid tree
   * @throws CloneNotSupportedException
   */
  public NTv2SubGrid[] getSubGridTree() throws CloneNotSupportedException {
    NTv2SubGrid[] clone = new NTv2SubGrid[topLevelSubGrid.length];
    for (int i = 0; i < topLevelSubGrid.length; i++) {
      clone[i] = (NTv2SubGrid) topLevelSubGrid[i].clone();
    }
    return clone;
  }

  public String getFromEllipsoid() {
    return fromEllipsoid;
  }

  public String getToEllipsoid() {
    return toEllipsoid;
  }
}
