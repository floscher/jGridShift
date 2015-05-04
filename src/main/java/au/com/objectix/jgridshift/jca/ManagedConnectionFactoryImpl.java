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

import java.io.PrintWriter;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.resource.ResourceException;
import javax.resource.NotSupportedException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;

import au.com.objectix.jgridshift.GridShiftFile;

// TO DO: implement bound standard properties

public class ManagedConnectionFactoryImpl implements ManagedConnectionFactory {

    public static final String RANDOM_FILE = "RandomFile";
    public static final String FILE_STREAM = "FileStream";
    public static final String JDBC_STREAM = "JdbcStream"; // not implemented yet
    
    private String dataSource;
    private String gridShiftFile;
    private boolean loadAccuracy = false;
    private String streamLoadedJniName = "java:/ra/jgridshiftfile";

    private transient PrintWriter log;
    
	public Object createConnectionFactory(ConnectionManager cm) throws ResourceException {
		return new ConnectionFactoryImpl(cm, this);
	}

	public Object createConnectionFactory() throws ResourceException {
        throw new NotSupportedException("Only Managed Connections are supported");
	}

	public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
	       throws ResourceException {
        if (RANDOM_FILE.equalsIgnoreCase(dataSource)) {
            return new ManagedConnectionImpl(gridShiftFile);
        } else if (FILE_STREAM.equalsIgnoreCase(dataSource)) {
            GridShiftFile gsf = null;
            InitialContext ic = null;
            try {
                ic = new InitialContext();
                gsf = (GridShiftFile)ic.lookup(streamLoadedJniName);
            } catch (NameNotFoundException nnfe) {
                try {
                    gsf = new GridShiftFile();
                    FileInputStream fis = new FileInputStream(gridShiftFile);
                    gsf.loadGridShiftFile(fis, loadAccuracy);
                    ic.bind(streamLoadedJniName, gsf);
                } catch (NameAlreadyBoundException nabe) {
                    // someone else got in sooner
                    try {
                        gsf = (GridShiftFile)ic.lookup(streamLoadedJniName);
                    } catch (NamingException ne) {
                        ne.printStackTrace(log);
                        ResourceException re = new ResourceException("Failed to find GridShiftFile as name: " + streamLoadedJniName);
                        re.setLinkedException(ne);
                        throw re;
                    }
                } catch (Exception e) {
                    e.printStackTrace(log);
                    ResourceException re = new ResourceException("Failed to bind GridShiftFile as name: " + streamLoadedJniName);
                    re.setLinkedException(e);
                    throw re;
                }
            } catch (Exception e) {
                e.printStackTrace(log);
                ResourceException re = new ResourceException("Failed to acquire stream loaded GridShiftFile");
                re.setLinkedException(e);
                throw re;
            }
            return new ManagedConnectionImpl(gsf);
        } else {
            throw new ResourceException("Data source type not recognized: " + dataSource);
        }
	}

	public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
		ManagedConnectionImpl match = null;
		Iterator iterator = connectionSet.iterator();
        if (iterator.hasNext()) {
            match = (ManagedConnectionImpl)iterator.next();
        }
		return match;
	}

	public void setLogWriter(PrintWriter log) throws ResourceException {
		this.log = log;
	}

	public PrintWriter getLogWriter() throws ResourceException {
		return log;
	}
    /**
     * @return
     */
    public String getGridShiftFile() {
        return gridShiftFile;
    }

    /**
     * @param string
     */
    public void setGridShiftFile(String string) {
        gridShiftFile = string;
    }
    /**
     * @return
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param string
     */
    public void setDataSource(String string) {
        dataSource = string;
    }

    /**
     * @return
     */
    public String getStreamLoadedJniName() {
        return streamLoadedJniName;
    }

    /**
     * @param string
     */
    public void setStreamLoadedJniName(String string) {
        streamLoadedJniName = string;
    }

    /**
     * @return
     */
    public Boolean isLoadAccuracy() {
        return Boolean.valueOf(loadAccuracy);
    }

    /**
     * @param b
     */
    public void setLoadAccuracy(Boolean b) {
        loadAccuracy = b.booleanValue();
    }

}