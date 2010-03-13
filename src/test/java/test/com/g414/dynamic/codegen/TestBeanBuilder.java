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
package test.com.g414.dynamic.codegen;

import org.example.test.Example1;
import org.example.test.Example2;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.g414.dynamic.codegen.BeanBuilder;
import com.g414.dynamic.codegen.ClassLoadUtil;

/**
 * Exercise the deserializer...
 */
@Test
public class TestBeanBuilder {
	public void testExample() throws Exception {
		Class<?> class1 = materializeClass("Ex1", Example1.class);
		Example1 inst1 = (Example1) class1.newInstance();

		class1.getMethod("setA", Integer.class).invoke(inst1, -101);
		class1.getMethod("setB", String.class).invoke(inst1, "TestString");

		Assert.assertEquals(inst1.getA().toString(), "-101");
		Assert.assertEquals(inst1.getB(), "TestString");

		Class<?> class2 = materializeClass("Ex2", Example2.class);
		Example2 inst2 = (Example2) class2.newInstance();

		class2.getMethod("setC", Long.class).invoke(inst2, 909L);
		Assert.assertEquals(inst2.getC().toString(), "909");
	}

	private Class<?> materializeClass(String name, Class<?> clazz) {
		BeanBuilder builder = new BeanBuilder(name);
		builder.implement(clazz);
		Class<?> out = ClassLoadUtil.loadClass(name, builder.build());
		return out;
	}
}
