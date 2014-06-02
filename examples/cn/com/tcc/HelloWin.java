package cn.com.tcc;

import java.io.File;
import java.io.IOException;

public class HelloWin {
	public static void main(String[] args) throws IOException {
		TCC.init("tcc");
		State state = new State();
		state.compile(new File("examples/hello_win.c"));
		state.run();
		state.delete();
	}
}
