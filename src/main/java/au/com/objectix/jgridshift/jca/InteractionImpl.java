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

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

import au.com.objectix.jgridshift.GridShift;

public class InteractionImpl implements Interaction {

	private GridShiftConnectionImpl c;

	public InteractionImpl(GridShiftConnectionImpl c) {
        this.c = c;
	}

	public void close() throws ResourceException {
		c = null;
	}

	public Connection getConnection() {
		return c;
	}

    /**
     * Execute a forward or reverse GridShift depending on the interaction spec value of isForward.
     * @param ispec A GridShiftInteractionSpec object to indicate forward or reverse shift.
     * @param input A GridShift object specifying the coordinate input to the GridShift. This object
     * is not updated by the method.
     * @param output A GridShift object holding the coordinate output from the GridShift. This object
     * is only updated if the shift is successful.
     * @return The GridShift is successful or not. If the input coordinate is outside the range of
     * the GridShift binary file, false is returned.
     */
	public boolean execute(InteractionSpec ispec, Record input, Record output) throws ResourceException {
        if (c == null) {
            throw new ResourceException("Interaction closed");
        } else {
            try {
                if (ispec instanceof GridShiftInteractionSpec) {
                    if (input instanceof GridShiftRecord) {
                        if (output instanceof GridShiftRecord) {
                            GridShiftRecord clone = (GridShiftRecord)input.clone();
                            boolean complete = false;
                            if (((GridShiftInteractionSpec)ispec).isForward()) {
                                complete = c.gridShiftForward(clone);
                            } else {
                                complete = c.gridShiftReverse(clone);
                            }
                            if (complete) {
                                ((GridShiftRecord)output).copy(clone);
                            }
                            return complete;
                        } else {
                            throw new ResourceException("Output Record is not a GridShift object");
                        }
                    } else {
                        throw new ResourceException("Input Record is not a GridShift object");
                    }                
                } else {
                    throw new ResourceException("Interaction Spec is not a GridShiftInteractionSpec object");
                }
            } catch (Exception e) {
                ResourceException re = new ResourceException("GridShift Failed: " + e.getMessage());
                re.setLinkedException(e);
                throw re;
            }
        }
	}

    /**
     * Execute a forward or reverse GridShift depending on the interaction spec value of isForward.
     * @param ispec A GridShiftInteractionSpec object to indicate forward or reverse shift.
     * @param input A GridShift object specifying the coordinate input to the GridShift. This object
     * is not updated by the method.
     * @return A GridShift holding the shifted coordinate. If the input coordinate is outside the range of
     * the GridShift binary file, null is returned.
     */
	public Record execute(InteractionSpec ispec, Record input) throws ResourceException {
        if (c == null) {
            throw new ResourceException("Interaction closed");
        } else {
            try {
                if (ispec instanceof GridShiftInteractionSpec) {
                    if (input instanceof GridShiftRecord) {
                            GridShiftRecord clone = (GridShiftRecord)input.clone();
                            boolean complete = false;
                            if (((GridShiftInteractionSpec)ispec).isForward()) {
                                complete = c.gridShiftForward(clone);
                            } else {
                                complete = c.gridShiftReverse(clone);
                            }
                            if (complete) {
                                return clone;
                            } else {
                                return null;
                            }
                    } else {
                        throw new ResourceException("Input Record is not a GridShift object");
                    }                
                } else {
                    throw new ResourceException("Interaction Spec is not a GridShiftInteractionSpec object");
                }
            } catch (Exception e) {
                ResourceException re = new ResourceException("GridShift Failed: " + e.getMessage());
                re.setLinkedException(e);
                throw re;
            }
        }
	}

	public ResourceWarning getWarnings() throws ResourceException {
		return null;
	}

	public void clearWarnings() throws ResourceException {
	}

}