package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.StringHolder;

import cn.com.tcc.TCC.ErrorFunction;

import com.sun.jna.Pointer;

public class CompileStringTest extends BaseTest {
	@Test
	public void compileString() {
		State state = new State();
		Assert.assertTrue(state.compileString("int main() {return 0;}"));
		Assert.assertEquals(0, state.run());
		state.delete();

		state = new State();
		final StringHolder msgHolder = new StringHolder();
		state.setErrorFunc(new ErrorFunction() {
			@Override
			public void callback(Pointer opaque, String msg) {
				msgHolder.value = msg;
			}
		});
		Assert.assertFalse(state
				.compileString("#include \"xxxx.h\"\nint main() {return 0;}"));
		waitErrorMessage(msgHolder);
		Assert.assertNotNull(msgHolder.value);
		String expected = "error: include file 'xxxx.h' not found";
		Assert.assertTrue(String.format(
				"Expected contains '%s' but actual is '%s'.", expected,
				msgHolder.value), msgHolder.value.contains(expected));
		state.delete();
	}
}
