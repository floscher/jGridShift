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

/**
 * A set of static utility methods for reading the NTv2 file format
 */
public final class NTv2Util {

  private NTv2Util() {
  }

  /**
   * Shorthand method for {@link #readBytes(InputStream, byte[], int)}.
   * Calls {@link #readBytes(InputStream, byte[], int)} with an unlimited number of bytes per invocation of
   * {@link InputStream#read(byte[], int, int)}.
   * @param in the {@link InputStream} to read from
   * @param buffer the byte array to write into
   * @throws IOException If the end of the {@link InputStream} is reached before {@code buffer.length} bytes are read
   *     or if the first byte cannot be read for any reason other than end of file, or if the input stream has been
   *     closed, or if some other I/O error occurs
   * @throws NullPointerException if {@code buffer} is null
   */
  public static void readBytes(InputStream in, byte[] buffer) throws IOException {
    readBytes(in, buffer, Integer.MAX_VALUE);
  }

  /**
   * Read a number of bytes from an InputStream into the given byte array.
   * The number of read bytes is determined by the length of the given byte array.
   * The number of bytes that are read per invocation of {@link InputStream#read(byte[], int, int)} can be limited by
   * the parameter {@code maxBytesAtOnce}.
   * @param in the {@link InputStream} to read from
   * @param buffer the byte array to write into (must not be {@code null})
   * @param maxBytesAtOnce
   *     the maximum number of bytes that is read per invocation of {@link InputStream#read(byte[], int, int)}
   * @throws IOException If the end of the {@link InputStream} is reached before {@code buffer.length} bytes are read
   *     or if the first byte cannot be read for any reason other than end of file, or if the input stream has been
   *     closed, or if some other I/O error occurs
   * @throws NullPointerException if {@code buffer} is null
   */
  public static void readBytes(InputStream in, byte[] buffer, int maxBytesAtOnce) throws IOException {
    if (buffer == null) {
      throw new NullPointerException("The byte buffer for reading from InputStream must not be null!");
    }

    int pos = 0;
    while (pos < buffer.length) {
      int diff = in.read(buffer, pos, Math.min(buffer.length - pos, Math.max(1, maxBytesAtOnce)));
      if (diff < 0) {
        throw new IOException(String.format(
          "Could only read %d of %d bytes, because the InputStream ended unexpectedly!", pos+diff, buffer.length
        ));
      }
      pos += diff;
    }
  }

  /**
   * Get a Little Endian int from four bytes of a byte array
   * @param b the byte array
   * @param i the index of the first data byte in the array
   * @return the int
   */
  public static int getIntLE(byte[] b, int i) {
    return (b[i++] & 0x000000FF) | ((b[i++] << 8) & 0x0000FF00) | ((b[i++] << 16) & 0x00FF0000) | (b[i] << 24);
  }

  /**
   * Get a Big Endian int from four bytes of a byte array
   * @param b the byte array
   * @param i the index of the first data byte in the array
   * @return the int
   */
  public static int getIntBE(byte[] b, int i) {
    return (b[i++] << 24) | ((b[i++] << 16) & 0x00FF0000) | ((b[i++] << 8) & 0x0000FF00) | (b[i] & 0x000000FF);
  }

  /**
   * Get an int from the first 4 bytes of a byte array,
   * in either Big Endian or Little Endian format.
   * @param b the byte array
   * @param bigEndian is the byte array Big Endian?
   * @return the int
   */
  public static int getInt(byte[] b, boolean bigEndian) {
    if (bigEndian) {
      return getIntBE(b, 0);
    } else {
      return getIntLE(b, 0);
    }
  }

  /**
   * Get a float from the first 4 bytes of a byte array,
   * in either Big Endian or Little Endian format.
   * @param b the byte array
   * @param bigEndian is the byte array Big Endian?
   * @return the float
   */
  public static float getFloat(byte[] b, boolean bigEndian) {
    int i = 0;
    if (bigEndian) {
      i = getIntBE(b, 0);
    } else {
      i = getIntLE(b, 0);
    }
    return Float.intBitsToFloat(i);
  }

  /**
   * Get a double from the first 8 bytes of a byte array,
   * in either Big Endian or Little Endian format.
   * @param b the byte array
   * @param bigEndian is the byte array Big Endian?
   * @return the double
   */
  public static double getDouble(byte[] b, boolean bigEndian) {
    int i = 0;
    int j = 0;
    if (bigEndian) {
      i = getIntBE(b, 0);
      j = getIntBE(b, 4);
    } else {
      i = getIntLE(b, 4);
      j = getIntLE(b, 0);
    }
    long l = ((long)i << 32) |
    (j & 0x00000000FFFFFFFFL);
    return Double.longBitsToDouble(l);
  }

  /**
   * Does the current VM support the New IO api
   * @return true or false
   */
  public static boolean isNioAvailable() {
    boolean nioAvailable = false;
    try {
      Class.forName("java.nio.channels.FileChannel");
      nioAvailable = true;
    } catch (NoClassDefFoundError | ClassNotFoundException cnfe) {
    }
    return nioAvailable;
  }
}
