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

import javax.resource.cci.Connection;
import javax.resource.ResourceException;
import au.com.objectix.jgridshift.GridShift;

public interface GridShiftConnection extends Connection {
    
    
    /**
     * This is a non CCI method to facilitate more compact usage of the jGridshift API
     * <p>Shift a coordinate in the Forward direction of the Grid Shift File.
     * 
     * @param gs A GridShift object containing the coordinate to shift
     * @return True if the coordinate is within a Sub Grid, false if not
     * @throws ResourceException
     */
    public boolean gridShiftForward(GridShift gs) throws ResourceException;
    
    /**
     * This is a non CCI method to facilitate more compact usage of the jGridshift API
     * <p>Shift a coordinate in the Reverse direction of the Grid Shift File.
     * 
     * @param gs A GridShift object containing the coordinate to shift
     * @return True if the coordinate is within a Sub Grid, false if not
     * @throws ResourceException
     */
    public boolean gridShiftReverse(GridShift gs) throws ResourceException;
}
