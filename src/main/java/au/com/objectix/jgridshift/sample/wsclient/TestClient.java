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
package au.com.objectix.jgridshift.sample.wsclient;

import au.com.objectix.jgridshift.GridShift;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class TestClient {

    /**
     * Supply test coordinates as lat,lon args eg -37.0,145.0 -22.0,135.0
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception{	
		try {
            String endpoint = "http://localhost:8080/jboss-net/services/JGridShiftService";
            Service service = new Service();
            Call call = (Call)service.createCall();
            call.setTargetEndpointAddress(new java.net.URL(endpoint));
            call.setOperationName("gridShiftForward");
            for (int i = 0; i < args.length; i++) {
                doit(call, args[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
    private static void doit(Call call, String latlon) throws Exception {
        int comma = latlon.indexOf(',');
        long start = System.currentTimeMillis();
        GridShift gs = new GridShift();
        gs.setLatDegrees(Double.parseDouble(latlon.substring(0, comma)));
        gs.setLonPositiveEastDegrees(Double.parseDouble(latlon.substring(comma + 1)));
        Double latSec = new Double(gs.getLatSeconds());
        Double lonSec = new Double(gs.getLonPositiveWestSeconds());
        String result = (String) call.invoke( new Object[] {latSec, lonSec} );
        if (result == null) {
            System.out.println("No result");
        } else {
            comma = result.indexOf(',');
            gs.setLatSeconds(Double.parseDouble(result.substring(0, comma)));
            gs.setLonPositiveWestSeconds(Double.parseDouble(result.substring(comma + 1)));
            System.out.println("Lat: " + gs.getLatDegrees() + " Lon: " + gs.getLonPositiveEastDegrees() + " Time: " + (System.currentTimeMillis() - start));
        }        
    }
} 
