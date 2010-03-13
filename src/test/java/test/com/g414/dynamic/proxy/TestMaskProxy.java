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

import org.example.test.Example1;
import org.example.test.Example2;
import org.example.test.Example3;
import org.example.test.Example3Impl;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.g414.dynamic.proxy.MaskProxy;

@Test
public class TestMaskProxy {
    public void testMaskProxy() {
        Example3Impl impl = new Example3Impl();

        Example1 ex1 = MaskProxy.newProxyInstance(Example1.class, impl);
        Assert.assertEquals("4660", ex1.getA().toString());
        Assert.assertEquals("Foo", ex1.getB());

        Example2 ex2 = MaskProxy.newProxyInstance(Example2.class, impl);
        Assert.assertEquals("305419896", ex2.getC().toString());

        Example3 ex3 = MaskProxy.newProxyInstance(Example3.class, impl);
        Assert.assertEquals("4660", ex3.getA().toString());
        Assert.assertEquals("Foo", ex3.getB());
        Assert.assertEquals("305419896", ex3.getC().toString());
    }
}
