package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.StringHolder;

import cn.com.tcc.TCC.ErrorFunction;

import com.sun.jna.Function;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

public class SetLibPathTest extends BaseTest {
	@Test
	public void setLibPath() {
		State state = new State();
		Assert.assertTrue(state.compileString(PROGRAM_ADD));
		Assert.assertTrue(state.relocateAuto());
		Function addFunc = state.getFunction("add");
		Assert.assertNotNull(addFunc);
		Assert.assertEquals(3, addFunc.invokeInt(new Object[] { 1, 2 }));
		state.delete();

		// error lib path
		state = new State();
		state.setLibPath(System.getProperty("java.io.tmpdir"));
		Assert.assertTrue(state.compileString(PROGRAM_ADD));
		if (Platform.isWindows()
				|| "linux/arm".equals(TCC.getNativeLibraryResourcePrefix(
						TCC.getOS(TCC.getOSName()),
						System.getProperty("os.arch"),
						System.getProperty("os.name")))) {
			Assert.assertTrue(state.relocateAuto());
			addFunc = state.getFunction("add");
			Assert.assertNotNull(addFunc);
			Assert.assertEquals(5, addFunc.invokeInt(new Object[] { 2, 3 }));
		} else {
			final StringHolder msgHolder = new StringHolder();
			state.setErrorFunc(new ErrorFunction() {
				@Override
				public void callback(Pointer opaque, String msg) {
					msgHolder.value = msg;
				}
			});
			Assert.assertFalse(state.relocateAuto());
			waitErrorMessage(msgHolder);
			Assert.assertNotNull(msgHolder.value);
			String expected = "libtcc1.a' not found";
			Assert.assertTrue(String.format(
					"Expected contains '%s' but actual is '%s'.", expected,
					msgHolder.value), msgHolder.value.contains(expected));
		}
		state.delete();

		state = new State();
		state.setLibPath(TCC.getLibPath());
		Assert.assertTrue(state.compileString(PROGRAM_ADD));
		Assert.assertTrue(state.relocateAuto());
		addFunc = state.getFunction("add");
		Assert.assertNotNull(addFunc);
		Assert.assertEquals(7, addFunc.invokeInt(new Object[] { 3, 4 }));
		state.delete();
	}
}
