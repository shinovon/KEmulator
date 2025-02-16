/**
 *  BlueCove - Java library for Bluetooth
 *
 *  Java docs licensed under the Apache License, Version 2.0
 *  http://www.apache.org/licenses/LICENSE-2.0
 *   (c) Copyright 2001, 2002 Motorola, Inc.  ALL RIGHTS RESERVED.
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *  @version $Id: DataElement.java 2530 2008-12-09 18:52:53Z skarzhevskyy $
 */
package javax.bluetooth;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Please refer JSR-82
 */

public class DataElement {

	/**
	 * Please refer JSR-82
	 */
	public static final int NULL = 0x0000;

	/**
	 * Please refer JSR-82
	 */
	public static final int U_INT_1 = 0x0008;

	/**
	 * Please refer JSR-82
	 */
	public static final int U_INT_2 = 0x0009;

	/**
	 * Please refer JSR-82
	 */
	public static final int U_INT_4 = 0x000A;

	/**
	 * Please refer JSR-82
	 */
	public static final int U_INT_8 = 0x000B;

	/**
	 * Please refer JSR-82
	 */
	public static final int U_INT_16 = 0x000C;

	/**
	 * Please refer JSR-82
	 */
	public static final int INT_1 = 0x0010;

	/**
	 * Please refer JSR-82
	 */
	public static final int INT_2 = 0x0011;

	/**
	 * Please refer JSR-82
	 */
	public static final int INT_4 = 0x0012;

	/**
	 * Please refer JSR-82
	 */
	public static final int INT_8 = 0x0013;

	/**
	 * Please refer JSR-82
	 */
	public static final int INT_16 = 0x0014;

	/**
	 * Please refer JSR-82
	 */
	public static final int URL = 0x0040;

	/**
	 * Please refer JSR-82
	 */
	public static final int UUID = 0x0018;

	/**
	 * Please refer JSR-82
	 */
	public static final int BOOL = 0x0028;

	/**
	 * Please refer JSR-82
	 */
	public static final int STRING = 0x0020;

	/**
	 * Please refer JSR-82
	 */
	public static final int DATSEQ = 0x0030;

	/**
	 * Please refer JSR-82
	 */
	public static final int DATALT = 0x0038;

	private Object value;

	private int valueType;

	/**
	 * Please refer JSR-82
	 */
	public DataElement(int valueType) {
		switch (valueType) {
			case NULL:
				value = null;
				break;
			case DATALT:
			case DATSEQ:
				value = new Vector();
				break;
			default:
				throw new IllegalArgumentException("valueType " + typeToString(valueType)
						+ " is not DATSEQ, DATALT or NULL");
		}

		this.valueType = valueType;
	}

	/**
	 * Please refer JSR-82
	 */
	public DataElement(boolean bool) {
		value = bool ? Boolean.TRUE : Boolean.FALSE;
		valueType = BOOL;
	}

	/**
	 * Please refer JSR-82
	 */
	public DataElement(int valueType, long value) {
		switch (valueType) {
			case U_INT_1:
				if (value < 0 || value > 0xff) {
					throw new IllegalArgumentException(value + " not U_INT_1");
				}
				break;
			case U_INT_2:
				if (value < 0 || value > 0xffff) {
					throw new IllegalArgumentException(value + " not U_INT_2");
				}
				break;
			case U_INT_4:
				if (value < 0 || value > 0xffffffffl) {
					throw new IllegalArgumentException(value + " not U_INT_4");
				}
				break;
			case INT_1:
				if (value < -0x80 || value > 0x7f) {
					throw new IllegalArgumentException(value + " not INT_1");
				}
				break;
			case INT_2:
				if (value < -0x8000 || value > 0x7fff) {
					throw new IllegalArgumentException(value + " not INT_2");
				}
				break;
			case INT_4:
				if (value < -0x80000000 || value > 0x7fffffff) {
					throw new IllegalArgumentException(value + " not INT_4");
				}
				break;
			case INT_8:
				// Not boundaries tests
				break;
			default:
				throw new IllegalArgumentException("type " + typeToString(valueType) + " can't be represented long");
		}

		this.value = new Long(value);
		this.valueType = valueType;
	}

	/**
	 * Please refer JSR-82
	 */
	public DataElement(int valueType, Object value) {
		if (value == null) {
			throw new IllegalArgumentException("value param is null");
		}
		switch (valueType) {
			case URL:
			case STRING:
				if (!(value instanceof String)) {
					throw new IllegalArgumentException("value param should be String");
				}
				break;
			case UUID:
				if (!(value instanceof UUID)) {
					throw new IllegalArgumentException("value param should be UUID");
				}
				break;
			case U_INT_8:
				if (!(value instanceof byte[]) || ((byte[]) value).length != 8) {
					throw new IllegalArgumentException("value param should be byte[8]");
				}
				break;
			case U_INT_16:
			case INT_16:
				if (!(value instanceof byte[]) || ((byte[]) value).length != 16) {
					throw new IllegalArgumentException("value param should be byte[16]");
				}
				break;
			default:
				throw new IllegalArgumentException("type " + typeToString(valueType) + " can't be represented by Object");
		}
		this.value = value;
		this.valueType = valueType;
	}

	/**
	 * Please refer JSR-82
	 */
	public void addElement(DataElement elem) {
		if (elem == null) {
			throw new NullPointerException("elem param is null");
		}
		switch (valueType) {
			case DATALT:
			case DATSEQ:
				((Vector) value).addElement(elem);
				break;
			default:
				throw new ClassCastException("DataType is not DATSEQ or DATALT");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public void insertElementAt(DataElement elem, int index) {
		if (elem == null) {
			throw new NullPointerException("elem param is null");
		}
		switch (valueType) {
			case DATALT:
			case DATSEQ:
				((Vector) value).insertElementAt(elem, index);
				break;
			default:
				throw new ClassCastException("DataType is not DATSEQ or DATALT");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public int getSize() {
		switch (valueType) {
			case DATALT:
			case DATSEQ:
				return ((Vector) value).size();
			default:
				throw new ClassCastException("DataType is not DATSEQ or DATALT");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public boolean removeElement(DataElement elem) {
		if (elem == null) {
			throw new NullPointerException("elem param is null");
		}
		switch (valueType) {
			case DATALT:
			case DATSEQ:
				return ((Vector) value).removeElement(elem);
			default:
				throw new ClassCastException("DataType is not DATSEQ or DATALT");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public int getDataType() {
		return valueType;
	}

	/**
	 * Please refer JSR-82
	 */
	public long getLong() {
		switch (valueType) {
			case U_INT_1:
			case U_INT_2:
			case U_INT_4:
			case INT_1:
			case INT_2:
			case INT_4:
			case INT_8:
				return ((Long) value).longValue();
			default:
				throw new ClassCastException("DataType is not INT");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public boolean getBoolean() {
		if (valueType == BOOL) {
			return ((Boolean) value).booleanValue();
		} else {
			throw new ClassCastException("DataType is not BOOL");
		}
	}

	/**
	 * Please refer JSR-82
	 */
	public Object getValue() {
		switch (valueType) {
			case URL:
			case STRING:
			case UUID:
				return value;
			case U_INT_8:
			case U_INT_16:
			case INT_16:
				// Modifying the returned Object will not change this DataElemen
				return ((byte[]) value).clone();
			case DATSEQ:
			case DATALT:
				return ((Vector) value).elements();
			default:
				throw new ClassCastException("DataType is simple java type");
		}
	}

	private static String typeToString(int type) {
		switch (type) {
			case DataElement.NULL:
				return "NULL";
			case DataElement.U_INT_1:
				return "U_INT_1";
			case DataElement.U_INT_2:
				return "U_INT_2";
			case DataElement.U_INT_4:
				return "U_INT_4";
			case DataElement.INT_1:
				return "INT_1";
			case DataElement.INT_2:
				return "INT_2";
			case DataElement.INT_4:
				return "INT_4";
			case DataElement.INT_8:
				return "INT_8";
			case DataElement.INT_16:
				return "INT_16";
			case DataElement.URL:
				return "URL";
			case DataElement.STRING:
				return "STRING";
			case DataElement.UUID:
				return "UUID";
			case DataElement.DATSEQ:
				return "DATSEQ";
			case DataElement.BOOL:
				return "BOOL";
			case DataElement.DATALT:
				return "DATALT";
			default:
				return "Unknown" + type;
		}
	}

	/**
	 * Non JSR-82 function.
	 *
	 * @deprecated Use ((Object)dataElement).toString() if you want your
	 *             application to run in MDIP profile
	 */
	public String toString() {
		switch (valueType) {
			case U_INT_1:
			case U_INT_2:
			case U_INT_4:
			case INT_1:
			case INT_2:
			case INT_4:
			case INT_8:
				return typeToString(valueType) + " 0x" + Long.toHexString(((Long) value).longValue());
			case BOOL:
			case URL:
			case STRING:
			case UUID:
				return typeToString(valueType) + " " + value.toString();
			case U_INT_8:
			case U_INT_16:
			case INT_16: {
				byte[] b = (byte[]) value;

				StringBuffer buf = new StringBuffer();
				buf.append(typeToString(valueType)).append(" ");

				for (int i = 0; i < b.length; i++) {
					buf.append(Integer.toHexString(b[i] >> 4 & 0xf));
					buf.append(Integer.toHexString(b[i] & 0xf));
				}

				return buf.toString();
			}
			case DATSEQ: {
				StringBuffer buf = new StringBuffer("DATSEQ {\n");

				for (Enumeration e = ((Vector) value).elements(); e.hasMoreElements();) {
					buf.append(e.nextElement());
					buf.append("\n");
				}

				buf.append("}");

				return buf.toString();
			}
			case DATALT: {
				StringBuffer buf = new StringBuffer("DATALT {\n");

				for (Enumeration e = ((Vector) value).elements(); e.hasMoreElements();) {
					buf.append(e.nextElement());
					buf.append("\n");
				}

				buf.append("}");

				return buf.toString();
			}
			default:
				return "Unknown" + valueType;
		}
	}
}
