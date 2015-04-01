/**
 * Copyright (c) 2015 MapR, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jackhammer.tests.json;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jackhammer.json.JsonRecordWriter;
import org.jackhammer.tests.BaseTest;
import org.jackhammer.types.Interval;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TestJsonRecordWriter extends BaseTest {

  private byte[] getByteArray(int size) {
    byte[] bytes = new byte[size];
    for (int i = 0; i < bytes.length; ++i) {
      bytes[i] = (byte) i;
    }
    return bytes;
  }

  @Test
  public void testAllTypes() {

    JsonRecordWriter jsonWriter = new JsonRecordWriter();
    jsonWriter.put("boolean", true);
    jsonWriter.put("string", "santanu");
    jsonWriter.put("bytefield", (byte) 16);
    jsonWriter.put("short", (short) 1000);
    jsonWriter.put("integer", 32000);
    jsonWriter.put("long", 123456789L);
    jsonWriter.put("float", 10.123f);
    jsonWriter.put("double", 10.12345678d);
    jsonWriter.put("decimal1", new BigDecimal(12345.6789));
    jsonWriter.putDecimal("decimal2", (long) 13456, 4);
    jsonWriter.putDecimal("decimal3", 123456789L);
    jsonWriter.putDecimal("decimal4", 9876.54321d);
    jsonWriter.putDecimal("decimal5", 32700, 5);
    jsonWriter.put("binary1", getByteArray(5));
    jsonWriter.put("binary2", getByteArray(20), 10, 5);
    /* bytebuffer test */
    jsonWriter.put("binary3", ByteBuffer.wrap(getByteArray(10)));
    jsonWriter.put("date1", Date.valueOf("2010-01-10"));
    jsonWriter.put(("time1"), Time.valueOf("19:15:12"));
    jsonWriter.put("timestamp1", Timestamp.valueOf("2010-10-15 14:20:00"));
    jsonWriter.put("interval1", new Interval(10234567));

    // test array
    jsonWriter.putNewArray("array1");
    jsonWriter.add("santanu");
    jsonWriter.add(10.123f);
    jsonWriter.add((byte) 127);
    jsonWriter.add((short) 1000);
    jsonWriter.add(32000);
    jsonWriter.add(false);
    jsonWriter.add(123456789L);
    jsonWriter.add(32767);
    jsonWriter.addNull();
    jsonWriter.add(10.12345678d);
    jsonWriter.add(new BigDecimal(1234.567891));
    jsonWriter.add(Date.valueOf("2010-01-10"));
    jsonWriter.add(Time.valueOf("19:15:12"));
    jsonWriter.add(Timestamp.valueOf("2010-10-15 14:20:00"));
    jsonWriter.add(new Interval(10234567));
    jsonWriter.add(ByteBuffer.wrap(getByteArray(15)));
    jsonWriter.endArray();

    jsonWriter.build();
    System.out.println(jsonWriter.asUTF8String());

  }

  @Rule
  public ExpectedException exception = ExpectedException.none();

  /* disabling these negative test cases until we add manual error
   * checking for out of context writing on outputStream.
   */
  //@Test
  public void TestWrongArrayInsertion() {

    JsonRecordWriter jsonWriter = new JsonRecordWriter();
    jsonWriter.put("string", "santanu");
    exception.expect(IllegalStateException.class);
    jsonWriter.endMap();
    //exception.expect(IllegalStateException.class);
    jsonWriter.add((long)12345);
    jsonWriter.add((long)23456);
    jsonWriter.add((long)5555);
    //jsonWriter.EndDocument();
    System.out.println(jsonWriter.asUTF8String());

  }

  //@Test
  public void TestWrongMapInsertion() {

    JsonRecordWriter jsonWriter = new JsonRecordWriter();
    jsonWriter.putNewArray("array");
    jsonWriter.add((short) 1000);
    exception.expect(IllegalStateException.class);
    jsonWriter.put("string", "value");

  }

  @Test
  public void TestDecimalRange() {
    JsonRecordWriter jsonWriter = new JsonRecordWriter();
    jsonWriter.putDecimal("d1", Integer.MAX_VALUE, 7);
    jsonWriter.putDecimal("d2", Integer.MIN_VALUE, 7);
    jsonWriter.putDecimal("d3", Long.MAX_VALUE, 9);
    jsonWriter.putDecimal("d4", Long.MIN_VALUE, 9);
    jsonWriter.putDecimal("d5", Integer.MAX_VALUE, 15);
    jsonWriter.putDecimal("d6", Integer.MIN_VALUE, 15);
    jsonWriter.putDecimal("d7", Long.MAX_VALUE, 25);
    jsonWriter.putDecimal("d8", Long.MIN_VALUE, 25);
    jsonWriter.build();
    System.out.println(jsonWriter.asUTF8String());
  }
}
