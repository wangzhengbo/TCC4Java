package cn.com.tcc;

import java.io.File;
import java.io.IOException;

public class HelloDll {
	public static void main(String[] args) throws IOException {
		TCC.init("tcc");

		try {
			State state = new State();
			state.setOptions("-shared");
			state.setOutputType(OutputType.DLL);
			state.compile("examples/dll.c");
			state.outputFile("dll.dll");
			state.delete();

			State state2 = new State();
			state2.addFile("dll.def");
			state2.compile(new File("examples/hello_dll.c"));
			state2.run();
			state2.delete();
		} finally {
			new File("dll.def").delete();
			new File("dll.dll").delete();
		}

		State state = new State();
		state.setOptions("-shared");
		state.setOutputType(OutputType.MEMORY);
		state.compile("examples/dll.c");
		state.compile(new File("examples/hello_dll.c"));
		state.run();
		state.delete();
	}
}
