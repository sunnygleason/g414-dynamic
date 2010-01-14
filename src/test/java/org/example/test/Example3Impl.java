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
package org.example.test;

/**
 * Simple implementation class that may be serialized as any of 2 interfaces (or
 * itself).
 */
public class Example3Impl implements Example3 {
	public Integer getA() {
		return Integer.valueOf(0x1234);
	}

	public String getB() {
		return "Foo";
	}

	public Long getC() {
		return Long.valueOf(0x12345678L);
	}

	@Override
	public int getD() {
		return 0xCAFEBABE;
	}

	@Override
	public Example1 getNested() {
		return new Example1() {
			@Override
			public String getB() {
				return "nested";
			}

			@Override
			public Integer getA() {
				return -1;
			}
		};
	}
}
