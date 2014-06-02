package cn.com.tcc;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class OutputFileTest extends BaseTest {
	@Test
	public void outputFile() throws IOException {
		State state = new State();
		Assert.assertTrue(state.setOutputType(OutputType.OBJ));
		Assert.assertTrue(state.compile("test/outputFileExe.c"));
		File file = new File("test/outputFileExe.obj");
		if (file.exists()) {
			Assert.assertTrue(file.delete());
		}
		Assert.assertTrue(state.outputFile(file));
		state.delete();

		state = new State();
		Assert.assertTrue(state.addFile(file));
		Assert.assertEquals(0, state.run("a", "b", "c"));
		state.delete();
	}
}
