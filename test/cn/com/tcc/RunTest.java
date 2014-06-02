package cn.com.tcc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class RunTest extends BaseTest {
	@Test
	public void run() throws IOException {
		State state = new State();
		Assert.assertTrue(state.compileString("int main() { return 0; }"));
		Assert.assertEquals(0, state.run());
		state.delete();

		state = new State();
		Assert.assertTrue(state.compileString("int main() { return 1; }"));
		Assert.assertEquals(1, state.run());
		state.delete();

		state = new State();
		Assert.assertTrue(state.compileString("int main() { return 255; }"));
		Assert.assertEquals(255, state.run());
		state.delete();

		state = new State();
		Assert.assertTrue(state
				.compileString("int main(int argc, char *argv[]) { return argc; }"));
		Assert.assertEquals(3, state.run("a", "b", "c"));
		state.delete();

		state = new State();
		Assert.assertTrue(state.compile("test/run.c"));
		Assert.assertEquals(6, state.run("1", "2", "3"));
		state.delete();
	}
}
