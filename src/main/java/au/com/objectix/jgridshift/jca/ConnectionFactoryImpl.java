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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionSpec;
import javax.resource.cci.RecordFactory;
import javax.resource.cci.ResourceAdapterMetaData;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;

public class ConnectionFactoryImpl implements GridShiftConnectionFactory {

  private static final long serialVersionUID = -1858828671830332335L;
  private Reference reference;
  private ConnectionManager cm;
  private ManagedConnectionFactory mcf;

  public ConnectionFactoryImpl(ConnectionManager cm, ManagedConnectionFactory mcf) {
    this.cm = cm;
    this.mcf = mcf;
  }

  public GridShiftConnection getGridShiftConnection() throws ResourceException {
    return (GridShiftConnection) cm.allocateConnection(mcf, null);
  }

  public Connection getConnection() throws ResourceException {
    return (Connection)getGridShiftConnection();
  }

  public Connection getConnection(ConnectionSpec connectionSpec) throws ResourceException {
    return (Connection)getGridShiftConnection();
  }

  public RecordFactory getRecordFactory() throws ResourceException {
    throw new NotSupportedException("Record Factory not supported");
  }

  public ResourceAdapterMetaData getMetaData() throws ResourceException {
    return new ResourceAdapterMetaDataImpl();
  }

  public void setReference(Reference reference) {
    this.reference = reference;
  }

  public Reference getReference() throws NamingException {
    return reference;
  }
}
