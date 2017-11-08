package reegnz.jmh;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Benchmark)
public class Casting {

	private Map<String,String> map;
	private Object object;

	@Setup
	public void setup() {
		map = new HashMap<>();
		object = map;
		map.put("a", "");
	}

	@Benchmark
	public void empty() {
	}

	@Benchmark
	public Object baseline() {
		return map.get("a");
	}

	@Benchmark
	public Object casting() {
		return ((Map)object).get("a");
	}

	@Benchmark
	public Object wrongcasting() {
		try {
			return ((List)object);
		} catch (Exception e) {}
		return null;
	}
}
