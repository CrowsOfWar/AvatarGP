/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.crowsofwar.gorecore.tree;

import com.crowsofwar.gorecore.tree.TreeCommandException.Reason;

public interface ITypeConverter<T> {
	
	public static final ITypeConverter<Integer> CONVERTER_INTEGER = new ITypeConverter<Integer>() {
		@Override
		public Integer convert(String str) {
			try {
				return Integer.parseInt(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Integer");
			}
		}
		
		@Override
		public String toString(Integer obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Integer";
		}
	};
	
	public static final ITypeConverter<Float> CONVERTER_FLOAT = new ITypeConverter<Float>() {
		@Override
		public Float convert(String str) {
			try {
				return Float.parseFloat(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Float");
			}
		}
		
		@Override
		public String toString(Float obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Decimal number";
		}
	};
	
	public static final ITypeConverter<Double> CONVERTER_DOUBLE = new ITypeConverter<Double>() {
		@Override
		public Double convert(String str) {
			try {
				return Double.parseDouble(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Double");
			}
		}
		
		@Override
		public String toString(Double obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Decimal number";
		}
	};
	
	public static final ITypeConverter<Long> CONVERTER_LONG = new ITypeConverter<Long>() {
		@Override
		public Long convert(String str) {
			try {
				return Long.parseLong(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Long");
			}
		}
		
		@Override
		public String toString(Long obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Big Integer";
		}
	};
	
	public static final ITypeConverter<Short> CONVERTER_SHORT = new ITypeConverter<Short>() {
		@Override
		public Short convert(String str) {
			try {
				return Short.parseShort(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Short");
			}
		}
		
		@Override
		public String toString(Short obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Small-ish Integer";
		}
	};
	
	public static final ITypeConverter<Boolean> CONVERTER_BOOLEAN = new ITypeConverter<Boolean>() {
		@Override
		public Boolean convert(String str) {
			return Boolean.parseBoolean(str);
		}
		
		@Override
		public String toString(Boolean obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "True or false";
		}
	};
	
	public static final ITypeConverter<Byte> CONVERTER_BYTE = new ITypeConverter<Byte>() {
		@Override
		public Byte convert(String str) {
			try {
				return Byte.parseByte(str);
			} catch (Exception e) {
				throw new TreeCommandException(Reason.CANT_CONVERT, str, "Byte");
			}
		}
		
		@Override
		public String toString(Byte obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Byte 0-255";
		}
	};
	
	public static final ITypeConverter<Character> CONVERTER_CHAR = new ITypeConverter<Character>() {
		@Override
		public Character convert(String str) {
			return str.charAt(0);
		}
		
		@Override
		public String toString(Character obj) {
			return obj.toString();
		}
		
		@Override
		public String getTypeName() {
			return "Character";
		}
	};
	
	public static final ITypeConverter<String> CONVERTER_STRING = new ITypeConverter<String>() {
		@Override
		public String convert(String str) {
			return str;
		}
		
		@Override
		public String toString(String obj) {
			return obj;
		}
		
		@Override
		public String getTypeName() {
			return "Text";
		}
	};
	
	T convert(String str);
	
	String toString(T obj);
	
	/**
	 * Get a human-readable type name.
	 * 
	 * @return
	 */
	String getTypeName();
	
}
