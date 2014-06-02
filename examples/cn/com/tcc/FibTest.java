package cn.com.tcc;

import java.io.IOException;

public class FibTest {
	public static void main(String[] args) throws IOException {
		TCC.init("tcc");

		State state = new State();
		state.compile("examples/fib.c");
		state.run();
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		state.run("fib");
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		state.run("fib", 5);
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		state.run("fib", 10);
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		state.run("fib", 20);
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		state.run("fib", 30);
		state.delete();

		state = new State();
		state.compile("examples/fib.c");
		long start = System.currentTimeMillis();
		state.run("fib", 43);
		long end = System.currentTimeMillis();
		state.delete();
		System.out.println("# end - start = " + (end - start));

		start = System.currentTimeMillis();
		System.out.println(fib(43));
		end = System.currentTimeMillis();
		System.out.println("## end - start = " + (end - start));
	}

	private static int fib(int n) {
		if (n <= 2)
			return 1;
		else
			return fib(n - 1) + fib(n - 2);
	}
}
