/*
 * Copyright (c) 2003, 2004 Objectix Pty Ltd  All rights reserved.
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
package au.com.objectix.jgridshift.jca;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import au.com.objectix.jgridshift.NTv2GridShift;
import au.com.objectix.jgridshift.NTv2GridShiftFile;

public class ManagedConnectionImpl implements ManagedConnection {

  private static String NO_TRANSACTION = "Transactions not supported";

  private PrintWriter log;
  private GridShiftConnectionImpl conn;
  private ArrayList<ConnectionEventListener> listenerList = new ArrayList<>();
  private boolean destroyed = false;
  private NTv2GridShiftFile gridShiftFile;
  private boolean randomFile = false;

  public ManagedConnectionImpl(String gsf) throws ResourceException {
    try {
      RandomAccessFile raf = new RandomAccessFile(gsf, "r");
      gridShiftFile = new NTv2GridShiftFile();
      gridShiftFile.loadGridShiftFile(raf);
      randomFile = true;
    } catch (IOException ioe) {
      ResourceException re = new ResourceException("Filed to open NTv2GridShiftFile: " + gsf);
      re.setLinkedException(ioe);
    }
  }

  public ManagedConnectionImpl(NTv2GridShiftFile gsf) throws ResourceException {
    gridShiftFile = gsf;
    randomFile = false;
  }

  public synchronized Object getConnection(Subject subject, ConnectionRequestInfo req) throws ResourceException {
    if (conn == null) {
      conn = new GridShiftConnectionImpl(this);
    }
    return conn;
  }

  public synchronized void destroy() throws ResourceException {
    destroyed = true;
    if (conn != null) {
      conn.disconnect();
    }
    conn = null;
    listenerList = null;
    try {
      if (gridShiftFile != null) {
        if (randomFile) {
          gridShiftFile.unload();
        }
        gridShiftFile = null;
      }
    } catch (IOException ioe) {
      ResourceException re = new ResourceException("Failed to close NTv2GridShiftFile");
      re.setLinkedException(ioe);
    }
  }

  public void cleanup() throws ResourceException {
    if (conn == null) {
      conn.disconnect();
    }
    conn = null;
  }

  public void associateConnection(Object connection) throws ResourceException {}

  public synchronized void addConnectionEventListener(ConnectionEventListener listener) {
    listenerList.add(listener);
  }

  public synchronized void removeConnectionEventListener(ConnectionEventListener listener) {
    listenerList.remove(listener);
  }

  public XAResource getXAResource() throws ResourceException {
    throw new NotSupportedException(NO_TRANSACTION);
  }

  public LocalTransaction getLocalTransaction() throws ResourceException {
    throw new NotSupportedException(NO_TRANSACTION);
  }

  public ManagedConnectionMetaData getMetaData() throws ResourceException {
    return new ManagedConnectionMetaDataImpl("jgridshift");
  }

  public void setLogWriter(PrintWriter log) throws ResourceException {
    this.log = log;
  }

  public PrintWriter getLogWriter() throws ResourceException {
    return log;
  }

  synchronized void close(Connection c) {
    if (listenerList != null) {
      ConnectionEvent evt = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
      evt.setConnectionHandle(c);
      for (int i = 0; i < listenerList.size(); i++) {
        ConnectionEventListener listener = listenerList.get(i);
        listener.connectionClosed(evt);
      }
    }
  }

  /**
   * This is a non CCI method to facilitate more compact usage of the jGridshift API
   * <p>Shift a coordinate in the Forward direction of the Grid Shift File.</p>
   *
   * @param gs A NTv2GridShift object containing the coordinate to shift
   * @return True if the coordinate is within a Sub Grid, false if not
   * @throws ResourceException
   */
  public boolean gridShiftForward(NTv2GridShift gs) throws ResourceException {
    try {
      return gridShiftFile.gridShiftForward(gs);
    } catch (IOException ioe) {
      ResourceException re = new ResourceException("Faled to shift coordinate");
      re.setLinkedException(ioe);
      throw re;
    }
  }

  /**
   * This is a non CCI method to facilitate more compact usage of the jGridshift API
   * <p>Shift a coordinate in the Reverse direction of the Grid Shift File.
   *
   * @param gs A NTv2GridShift object containing the coordinate to shift
   * @return True if the coordinate is within a Sub Grid, false if not
   * @throws ResourceException
   */
  public boolean gridShiftReverse(NTv2GridShift gs) throws ResourceException {
    try {
      return gridShiftFile.gridShiftReverse(gs);
    } catch (IOException ioe) {
      ResourceException re = new ResourceException("Faled to shift coordinate");
      re.setLinkedException(ioe);
      throw re;
    }
  }

  private void signalError(IOException ioe) {
    if (listenerList != null) {
      ConnectionEvent evt = new ConnectionEvent(this, ConnectionEvent.CONNECTION_ERROR_OCCURRED, ioe);
      evt.setConnectionHandle(conn);
      for (int i = 0; i < listenerList.size(); i++) {
        ConnectionEventListener listener = listenerList.get(i);
        listener.connectionErrorOccurred(evt);
      }
    }
  }
}
