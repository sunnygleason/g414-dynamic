/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.g414.dynamic.codegen;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class BeanBuilder implements Opcodes {
	protected Map<String, Class<?>> properties = new LinkedHashMap<String, Class<?>>();
	protected Map<String, ThrowMethodType> throwMethods = new LinkedHashMap<String, ThrowMethodType>();
	protected List<Class<?>> implementing = new ArrayList<Class<?>>();
	protected String className;
	protected String internalClass;

	public BeanBuilder(String className) {
		this.className = className;
		this.internalClass = JavaNameTypeUtils.getInternalClassName(className);
	}

	public BeanBuilder implement(Class<?> parent) {
		this.implementing.add(parent);

		for (Method m : parent.getMethods()) {
			if (m.getName().startsWith("get") || m.getName().startsWith("set")) {
				String name = JavaNameTypeUtils.getFieldName(m.getName());
				Class<?> propType = m.getName().startsWith("get") ? m
						.getReturnType() : m.getParameterTypes()[0];

				if (this.properties.containsKey(name)
						&& !this.properties.get(name).equals(propType)) {
					throw new IllegalArgumentException("Duplicate property");
				}

				addProperty(name, propType);
			} else {
				addThrow(m.getName(), m.getParameterTypes(), m.getReturnType(),
						UnsupportedOperationException.class);
			}
		}

		return this;
	}

	public BeanBuilder addProperty(String name, Class<?> type) {
		properties.put(name, type);

		return this;
	}

	public BeanBuilder addThrow(String name, Class<?>[] paramTypes,
			Class<?> returnType, Class<?> exceptionType) {
		this.throwMethods.put(name, new ThrowMethodType(name, paramTypes,
				returnType, exceptionType));

		return this;
	}

	public byte[] build() {
		ClassWriter cw = new ClassWriter(0);

		String[] parents = new String[implementing.size()];
		for (int i = 0; i < implementing.size(); i++) {
			parents[i] = JavaNameTypeUtils.getInternalClassName(implementing
					.get(i).getName());
		}
		cw.visit(V1_2, ACC_PUBLIC + ACC_SUPER, internalClass, null,
				"java/lang/Object", parents);
		cw.visitSource(className + ".java", null);
		BeanFunctions.generateDefaultConstructor(cw);

		for (Map.Entry<String, Class<?>> propEntry : this.properties.entrySet()) {
			String propName = propEntry.getKey();
			Class<?> propClass = propEntry.getValue();

			BeanFunctions.createField(cw, propName, propClass);
			BeanFunctions.createGetter(cw, internalClass, propName, propClass);
			BeanFunctions.createSetter(cw, internalClass, propName, propClass);
		}

		for (Map.Entry<String, ThrowMethodType> throwEntry : this.throwMethods
				.entrySet()) {
			ThrowMethodType thr = throwEntry.getValue();

			BeanFunctions.createThrow(cw, this.internalClass, throwEntry
					.getKey(), thr.paramTypes, thr.returnType,
					thr.exceptionType);
		}

		cw.visitEnd();
		return cw.toByteArray();
	}

	protected static class ThrowMethodType {
		public final String name;
		public final Class<?>[] paramTypes;
		public final Class<?> returnType;
		public final Class<?> exceptionType;

		public ThrowMethodType(String name, Class<?>[] paramTypes,
				Class<?> returnType, Class<?> exceptionType) {
			this.name = name;
			this.paramTypes = paramTypes;
			this.returnType = returnType;
			this.exceptionType = exceptionType;
		}
	}
}
