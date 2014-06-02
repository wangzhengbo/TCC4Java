package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;

public class AddFileTest extends BaseTest {
	@Test
	public void addFile() {
		State state = new State();
		Assert.assertTrue(state.addFile("test/addFile.c"));
		Assert.assertEquals(0, state.run());
		state.delete();

		state = new State();
		Assert.assertTrue(state.addFile("test/addFile.c"));
		Assert.assertEquals(1, state.run("1"));
		state.delete();

		state = new State();
		Assert.assertTrue(state.addFile("test/addFile.c"));
		Assert.assertEquals(3, state.run("a", "b", "c"));
		state.delete();
	}
}
