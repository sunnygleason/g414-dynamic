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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BeanFunctions implements Opcodes {
	public static void generateDefaultConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null,
				null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static void createField(ClassWriter cw, String fieldName,
			Class<?> fieldType) {
		String javaType = JavaNameTypeUtils.getLValue(fieldType);
		FieldVisitor fv = cw.visitField(0, fieldName, javaType, null, null);
		fv.visitEnd();
	}

	public static void createSetter(ClassWriter cw, String internalClassName,
			String fieldName, Class<?> fieldType) {
		String methodName = JavaNameTypeUtils.getSetterName(fieldName);
		String returnType = JavaNameTypeUtils.getLValue(fieldType);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "("
				+ returnType + ")V", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitFieldInsn(PUTFIELD, internalClassName, fieldName, returnType);
		mv.visitInsn(RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	public static void createGetter(ClassWriter cw, String internalClassName,
			String fieldName, Class<?> fieldType) {
		String methodName = JavaNameTypeUtils.getGetterName(fieldName);
		String returnType = JavaNameTypeUtils.getLValue(fieldType);
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, "()"
				+ returnType, null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, internalClassName, fieldName, returnType);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public static void createThrow(ClassWriter cw, String internalClassName,
			String methodName, Class<?>[] inTypes, Class<?> returnType,
			Class<?> exceptionType) {
		String rTypeName = JavaNameTypeUtils.getLValue(returnType);
		String exceptionName = JavaNameTypeUtils
				.getInternalClassName(exceptionType.getName());

		String sig = "(" + JavaNameTypeUtils.getArgumentsType(inTypes) + ")"
				+ rTypeName;

		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, sig, null,
				null);

		mv.visitTypeInsn(NEW, exceptionName);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, exceptionName, "<init>", "()V");
		mv.visitInsn(ATHROW);
		mv.visitMaxs(2, 1 + inTypes.length);
		mv.visitEnd();
	}
}
