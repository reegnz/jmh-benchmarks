/**
 * Copyright (c) 2017 Zoltán Reegn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package reegnz.jmh;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Fork(3)
public class ProxyInvocation {

	private Map<String,String> map;
	private Map<String,String> mapProxy;
	private Map<String,String> mapProxyLambda;

	@Setup
	public void setup() {
		map = new HashMap<>();
		map.put("a", "");
		mapProxy = createProxy();
		mapProxyLambda = createProxyLambda();
	}


	private Map<String,String> createProxyLambda() {
		return (Map<String,String>) Proxy.newProxyInstance(
				Map.class.getClassLoader(),
				new Class[] {Map.class},
				(proxy, method, args) -> {
					return method.invoke(map, args);
				}
			);
	}

	private Map<String, String> createProxy() {
		return (Map<String,String>) Proxy.newProxyInstance(
				Map.class.getClassLoader(),
				new Class[] {Map.class},
				new DelegatingHandler(map)
			);
	}

	private static final class DelegatingHandler implements InvocationHandler {
		private final Object delegate;

		DelegatingHandler(Object delegate) {
			this.delegate = delegate;
		}
		public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
			return method.invoke(delegate, args);
		}
	}

	@Benchmark
	public void empty() {
	}

	@Benchmark
	public String baseline() {
		return map.get("a");
	}

	@Benchmark
	public String uncachedProxy() {
		return createProxy().get("a");
	}

	@Benchmark
	public String cachedProxy() {
		return mapProxy.get("a");
	}

	@Benchmark
	public String uncachedProxyLambda() {
		return createProxyLambda().get("a");
	}

	@Benchmark
	public String cachedProxyLambda() {
		return mapProxyLambda.get("a");
	}
}
