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
package au.com.objectix.jgridshift.sample.session;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.resource.ResourceException;

import au.com.objectix.jgridshift.jca.GridShiftConnectionFactory;
import au.com.objectix.jgridshift.jca.GridShiftConnection;
import au.com.objectix.jgridshift.NTv2GridShift;

/**
 * Implementation of the sample jgridshift web service.
 *
 * @ejb:bean name="jgridshift/JGridShift"
 *           display-name="Grid Shift"
 *           type="Stateless"
 *           view-type="local"
 *           jndi-name="ejb/jgridshift/JGridShift"
 *
 * @jboss-net:web-service urn="JGridShiftService"
 */
public abstract class JGridShiftBean implements SessionBean {

  private static final long serialVersionUID = -7420580002147954692L;
  private GridShiftConnectionFactory gsFactory;

  /**
   * @ejb:interface-method view-type="local"
   * @jboss-net:web-method
   *
   * @return the shifted coordinate
   */
  public String gridShiftForward(Double latSeconds, Double lonPositiveWestSeconds) throws ResourceException {
    NTv2GridShift gs = new NTv2GridShift();
    gs.setLatSeconds(latSeconds.doubleValue());
    gs.setLonPositiveWestSeconds(lonPositiveWestSeconds.doubleValue());
    GridShiftConnection gsConn = null;
    try {
      gsConn = gsFactory.getGridShiftConnection();
      boolean success = gsConn.gridShiftForward(gs);
      String result = null;
      if (success) {
        result = "" + gs.getShiftedLatSeconds() + "," + gs.getShiftedLonPositiveWestSeconds();
      }
      return result;
    } finally {
      gsConn.close();
    }
  }

  /**
   * @ejb:interface-method view-type="local"
   * @jboss-net:web-method
   *
   * @return the shifted coordinate
   */
  public String gridShiftReverse(Double latSeconds, Double lonPositiveWestSeconds) throws ResourceException {
    NTv2GridShift gs = new NTv2GridShift();
    gs.setLatSeconds(latSeconds.doubleValue());
    gs.setLonPositiveWestSeconds(lonPositiveWestSeconds.doubleValue());
    GridShiftConnection gsConn = null;
    try {
      gsConn = gsFactory.getGridShiftConnection();
      boolean success = gsConn.gridShiftReverse(gs);
      String result = null;
      if (success) {
        result = "" + gs.getShiftedLatSeconds() + "," + gs.getShiftedLonPositiveWestSeconds();
      }
      return result;
    } finally {
      gsConn.close();
    }
  }

  /**
   * @ejb:interface-method view-type="local"
   * @jboss-net:web-method
   *
   * @return the shifted coordinate
   */
  public Coordinate gridShiftForwardTyped(Coordinate coord) throws ResourceException {
    NTv2GridShift gs = new NTv2GridShift();
    gs.setLatSeconds(coord.getLatSeconds());
    gs.setLonPositiveWestSeconds(coord.getLonPositiveWestSeconds());
    GridShiftConnection gsConn = null;
    try {
      gsConn = gsFactory.getGridShiftConnection();
      boolean success = gsConn.gridShiftForward(gs);
      if (success) {
        coord.setLatSeconds(gs.getShiftedLatSeconds());
        coord.setLonPositiveWestSeconds(gs.getShiftedLonPositiveWestSeconds());
        return coord;
      } else {
        return null;
      }
    } finally {
      gsConn.close();
    }
  }

  /**
   * @ejb:interface-method view-type="local"
   * @jboss-net:web-method
   *
   * @return the shifted coordinate
   */
  public Coordinate gridShiftReverseTyped(Coordinate coord) throws ResourceException {
    NTv2GridShift gs = new NTv2GridShift();
    gs.setLatSeconds(coord.getLatSeconds());
    gs.setLonPositiveWestSeconds(coord.getLonPositiveWestSeconds());
    GridShiftConnection gsConn = null;
    try {
      gsConn = gsFactory.getGridShiftConnection();
      boolean success = gsConn.gridShiftReverse(gs);
      if (success) {
        coord.setLatSeconds(gs.getShiftedLatSeconds());
        coord.setLonPositiveWestSeconds(gs.getShiftedLonPositiveWestSeconds());
        return coord;
      } else {
        return null;
      }
    } finally {
      gsConn.close();
    }
  }

  /**
   * @see javax.ejb.SessionBean#ejbActivate()
   */
  public void ejbActivate() throws EJBException, RemoteException {}

  /**
   * @see javax.ejb.SessionBean#ejbPassivate()
   */
  public void ejbPassivate() throws EJBException, RemoteException {}

  /**
   * @see javax.ejb.SessionBean#ejbRemove()
   */
  public void ejbRemove() throws EJBException, RemoteException {}

  /**
   * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
   */
  public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
    try {
      InitialContext ic = new InitialContext();
      gsFactory = (GridShiftConnectionFactory)ic.lookup("java:/ra/jgridshift");
    } catch (Exception e) {
      throw new EJBException("Failed to lookup NTv2GridShift Connection Factory", e);
    }
  }
}
