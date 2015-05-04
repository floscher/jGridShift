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

import java.util.ArrayList;
import java.io.IOException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Record;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionMetaData;
import javax.resource.cci.Interaction;
import javax.resource.cci.LocalTransaction;
import javax.resource.cci.ResultSetInfo;
import javax.resource.spi.ManagedConnection;

import au.com.objectix.jgridshift.GridShift;

public class GridShiftConnectionImpl implements GridShiftConnection {

	private static final String CLOSED = "Connection closed";
	private static final String NO_TRANSACTION = "Local transactions not supported";
	private static final String NO_RESULT_SET = "Result sets not supported";
	private ManagedConnectionImpl mc;

	public GridShiftConnectionImpl(ManagedConnectionImpl mc) {
		this.mc = mc;
	}

	void disconnect() {
		mc = null;
	}

	public Interaction createInteraction() throws ResourceException {
		if (mc != null) {
            return new InteractionImpl(this);
		} else {
			throw new ResourceException(CLOSED);
		}
	}

	public LocalTransaction getLocalTransaction() throws ResourceException {
		throw new NotSupportedException(NO_TRANSACTION);
	}

	public ConnectionMetaData getMetaData() throws ResourceException {
		if (mc != null) {
			return new ConnectionMetaDataImpl(mc.getMetaData());
		} else {
			throw new ResourceException(CLOSED);
		}
	}

	public ResultSetInfo getResultSetInfo() throws ResourceException {
		throw new NotSupportedException(NO_RESULT_SET);
	}

    public void close() throws ResourceException {
        if (mc != null) {
            mc.close(this);
        }
        mc = null;
    }
    
    /**
     * This is a non CCI method to facilitate more compact usage of the jGridshift API
     * <p>Shift a coordinate in the Forward direction of the Grid Shift File.
     * 
     * @param gs A GridShift object containing the coordinate to shift
     * @return True if the coordinate is within a Sub Grid, false if not
     * @throws IOException
     */
    public boolean gridShiftForward(GridShift gs) throws ResourceException {
        return mc.gridShiftForward(gs);
    }
    
    /**
     * This is a non CCI method to facilitate more compact usage of the jGridshift API
     * <p>Shift a coordinate in the Reverse direction of the Grid Shift File.
     * 
     * @param gs A GridShift object containing the coordinate to shift
     * @return True if the coordinate is within a Sub Grid, false if not
     * @throws IOException
     */
    public boolean gridShiftReverse(GridShift gs) throws ResourceException {
        return mc.gridShiftReverse(gs);
    }
}