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

import javax.resource.cci.Record;

import au.com.objectix.jgridshift.NTv2GridShift;

public class GridShiftRecord extends NTv2GridShift implements Record {

  private static final long serialVersionUID = 3303083935624698012L;
  private String name;
  private String shortDesc;

  /**
   * @see javax.resource.cci.Record#getRecordName()
   */
  public String getRecordName() {
    return name;
  }
  /**
   * @see javax.resource.cci.Record#setRecordName(java.lang.String)
   */
  public void setRecordName(String s) {
    name = s;
  }
  /**
   * @see javax.resource.cci.Record#getRecordShortDescription()
   */
  public String getRecordShortDescription() {
    return shortDesc;
  }
  /**
   * @see javax.resource.cci.Record#setRecordShortDescription(java.lang.String)
   */
  public void setRecordShortDescription(String s) {
    shortDesc = s;
  }
  /**
   * Clone this record
   */
  public Object clone() {
    Object clone = null;
    try {
      clone = super.clone();
    } catch (CloneNotSupportedException cnse) {}
    return clone;
  }
}
