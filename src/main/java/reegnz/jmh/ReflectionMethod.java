package reegnz.jmh;

import java.lang.reflect.Method;
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
public class ReflectionMethod {

	private Map<String,String> map;
	private Method method;
	private Method accessibleMethod;

	@Setup
	public void setup() {
		map = new HashMap<>();
		map.put("a", "");
		method = lookup();
		accessibleMethod = lookup();
		setAccessible(accessibleMethod);
	}

	private Method lookup() {
		try {
			return Map.class.getMethod("get", Object.class);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setAccessible(Method m) {
		try {
			m.setAccessible(true);
		} catch(Exception e) {
			throw new RuntimeException(e);
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
	public String cached() {
		return invoke(method);
	}

	@Benchmark
	public String cachedAccessible() {
		return invoke(accessibleMethod);
	}

	@Benchmark
	public String uncached() {
		Method m = lookup();
		return invoke(m);
	}

	@Benchmark
	public String uncachedAccessible() {
		Method m = lookup();
		setAccessible(m);
		return invoke(m);
	}

	@Benchmark
	@Fork(jvmArgsAppend = "-Dsun.reflect.inflationThreshold=2000000000")
	public String cachedNoInflation() {
		return invoke(method);
	}

	@Benchmark
	@Fork(jvmArgsAppend = "-Dsun.reflect.inflationThreshold=2000000000")
	public String cachedNoInflationAccessible() {
		return invoke(accessibleMethod);
	}

	@Benchmark
	@Fork(jvmArgsAppend = "-Dsun.reflect.inflationThreshold=2000000000")
	public String uncachedNoInflation() {
		Method m = lookup();
		return invoke(m);
	}

	@Benchmark
	@Fork(jvmArgsAppend = "-Dsun.reflect.inflationThreshold=2000000000")
	public String uncachedNoInflationAccessible() {
		Method m = lookup();
		setAccessible(m);
		return invoke(m);
	}

	private String invoke(Method m) {
		try {
			return (String) m.invoke(map, "a");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
