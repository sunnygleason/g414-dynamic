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

public class JavaNameTypeUtils {
    public static String getInternalClassName(String className) {
        return className.replace(".", "/");
    }

    public static String getFieldName(String getterMethodName) {
        char[] name = getterMethodName.substring(3).toCharArray();
        name[0] = Character.toLowerCase(name[0]);
        final String propName = new String(name);

        return propName;
    }

    public static String getLValue(Class<?> fieldType) {
        if (fieldType == null || fieldType.equals(void.class)) {
            return "V";
        }

        String plainR = fieldType.getName();
        String rType = JavaNameTypeUtils.getInternalClassName(plainR);
        String javaType = "L" + rType + ";";
        return javaType;
    }

    public static String getGetterName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
    }

    public static String getSetterName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
    }

    public static String getArgumentsType(Class<?>[] inTypes) {
        StringBuilder list = new StringBuilder();

        for (Class<?> clazz : inTypes) {
            list.append(getLValue(clazz));
        }

        return list.toString();
    }
}
