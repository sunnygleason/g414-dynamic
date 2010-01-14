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
package com.g414.dynamic.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An InvocationHandler that allows us to dispatch methods from several
 * interfaces in the specified order.
 */
@SuppressWarnings("unchecked")
public class DispatchProxy implements InvocationHandler {
	private final List<Class> ifaces;
	private final List<Object> delegates;
	private final Map<Method, TargetInvocation> dispatchMap;

	private final InvocationHandler finalHandler;

	public static final InvocationHandler UNSUPPORTED_OPERATION_HANDLER = new InvocationHandler() {
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			throw new UnsupportedOperationException();
		}
	};

	public static Object newProxyInstance(List<Class> ifaces,
			List<Object> delegates) {
		return Proxy.newProxyInstance(DispatchProxy.class.getClassLoader(),
				ifaces.toArray(new Class[ifaces.size()]), new DispatchProxy(
						ifaces, delegates));
	}

	public List<Class> getInterfaces() {
		return ifaces;
	}

	public DispatchProxy(List<Class> ifaces, List<Object> delegates) {
		List<Class> newIfaces = new ArrayList<Class>();
		newIfaces.addAll(ifaces);
		this.ifaces = Collections.unmodifiableList(newIfaces);

		List<Object> newDelegates = new ArrayList<Object>();
		newDelegates.addAll(delegates);
		this.delegates = Collections.unmodifiableList(delegates);

		Map<Method, TargetInvocation> dispatchMap = new LinkedHashMap<Method, TargetInvocation>();

		for (Class iface : this.ifaces) {
			for (Method method : iface.getMethods()) {
				if (!dispatchMap.containsKey(method)) {
					OBJECT: for (Object object : this.delegates) {
						try {
							Method targetMethod = object.getClass().getMethod(
									method.getName(),
									method.getParameterTypes());

							if (targetMethod != null
									&& ((method.getReturnType() != null
											&& targetMethod.getReturnType() != null && targetMethod
											.getReturnType().equals(
													method.getReturnType())))
									|| (method.getReturnType() == null && targetMethod
											.getReturnType() == null)) {
								dispatchMap.put(method, new TargetInvocation(
										targetMethod, object));
								break OBJECT;
							}
						} catch (NoSuchMethodException e) {
							// ignore
						}
					}
				}
			}
		}

		this.dispatchMap = Collections.unmodifiableMap(dispatchMap);
		System.out.println(this.dispatchMap);
		this.finalHandler = UNSUPPORTED_OPERATION_HANDLER;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		TargetInvocation target = dispatchMap.get(method);

		if (target == null) {
			if (method.getName().equals("equals") && args.length == 1) {
				return this.equals(args[0]);
			} else if (method.getName().equals("hashCode") && args == null) {
				return this.hashCode();
			} else if (method.getName().equals("toString") && args == null) {
				return this.toString();
			} else {
				return finalHandler.invoke(proxy, method, args);
			}
		}

		try {
			return target.method.invoke(target.object, args);
		} catch (IllegalArgumentException iae) {
		} catch (IllegalAccessException iae2) {
		} catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
		// allow SecurityException to go out unchecked

		return finalHandler.invoke(proxy, method, args);
	}

	public static class TargetInvocation {
		public final Method method;
		public final Object object;

		public TargetInvocation(Method targetMethod, Object targetObject) {
			this.method = targetMethod;
			this.object = targetObject;
		}

		@Override
		public String toString() {
			return "TargetInvocation{method=" + method.toString() + ", object="
					+ object.toString() + "}";
		}
	}
}
