package au.com.objectix.jgridshift;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;
/**
 * Test class for {@link NTv2Util}
 */
public class TestNTv2Util {

  /**
   * Test method for {@link NTv2Util#readBytes(InputStream, byte[])}.
   */
  @Test(expected = NullPointerException.class)
  public final void testReadBytesBufferNull() throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[9]);
    NTv2Util.readBytes(bais, null);
  }

  /**
   * Test method for {@link NTv2Util#readBytes(InputStream, byte[])}.
   */
  @Test(expected = IOException.class)
  public final void testReadBytesEarlyEnd() throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(new byte[9]);
    NTv2Util.readBytes(bais, new byte[10]);
  }

  /**
   * Test method for {@link NTv2Util#readBytes(InputStream, byte[])}.
   */
  @Test
  public final void testReadBytes() throws IOException {
    byte[] expected = new byte[]{-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7};
    ByteArrayInputStream bais = new ByteArrayInputStream(expected);
    byte[] actual = new byte[expected.length];
    NTv2Util.readBytes(bais, actual);
    assertArrayEquals(expected,actual);

    bais = new ByteArrayInputStream(expected);
    actual = new byte[expected.length];
    NTv2Util.readBytes(bais, actual, 2);
    assertArrayEquals(expected, actual);

    bais = new ByteArrayInputStream(expected);
    actual = new byte[expected.length];
    NTv2Util.readBytes(bais, actual, -1);
    assertArrayEquals(expected, actual);

    bais = new ByteArrayInputStream(expected);
    actual = new byte[expected.length];
    NTv2Util.readBytes(bais, actual, expected.length +5);
    assertArrayEquals(expected, actual);

    bais = new ByteArrayInputStream(expected);
    actual = new byte[expected.length / 2];
    NTv2Util.readBytes(bais, actual);
    assertArrayEquals(Arrays.copyOf(expected, expected.length / 2), actual);
  }
}
