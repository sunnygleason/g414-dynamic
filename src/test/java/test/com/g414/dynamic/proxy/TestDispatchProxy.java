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
package test.com.g414.dynamic.proxy;

import java.util.Arrays;

import junit.framework.TestCase;

import org.example.test.Example1;
import org.example.test.Example2;
import org.example.test.Example3;
import org.example.test.Example3Impl;
import org.example.test.Example4;
import org.example.test.Example4Impl;

import com.g414.dynamic.proxy.DispatchProxy;

public class TestDispatchProxy extends TestCase {
	public void testMaskProxy() {
		Example3Impl impl1 = new Example3Impl();
		Example4Impl impl2 = new Example4Impl();

		Object compound = DispatchProxy.newProxyInstance(Arrays
				.asList(new Class[] { Example1.class, Example2.class,
						Example3.class, Example4.class }), Arrays.asList(impl1,
				impl2));

		assertEquals("-1", impl2.getA().toString());

		Example1 ex1 = (Example1) compound;
		assertEquals("4660", ex1.getA().toString());
		assertEquals("Foo", ex1.getB());

		Example2 ex2 = (Example2) compound;
		assertEquals("305419896", ex2.getC().toString());

		Example3 ex3 = (Example3) compound;
		assertEquals("4660", ex3.getA().toString());
		assertEquals("Foo", ex3.getB());
		assertEquals("305419896", ex3.getC().toString());

		Example4 ex4 = (Example4) compound;
		// Example4 impl2 getA() shadowed by Example1 / impl1 getA()
		assertEquals("4660", ex4.getA().toString());
		assertEquals("4", ex4.getValue().toString());
		ex4.doIt();
		ex4.setAnotherValue();
	}
}
