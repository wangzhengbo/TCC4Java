package cn.com.tcc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.StringHolder;

import cn.com.tcc.TCC.ErrorFunction;

import com.sun.jna.Pointer;

public class SetOptionsTest extends BaseTest {
	@Test
	public void setOptoins() throws IOException {
		State state = new State();
		final StringHolder msgHolder = new StringHolder();
		state.setErrorFunc(new ErrorFunction() {
			@Override
			public void callback(Pointer opaque, String msg) {
				msgHolder.value = msg;
			}
		});
		Assert.assertFalse(state.compile("test/addIncludePath.c"));
		waitErrorMessage(msgHolder);
		Assert.assertNotNull(msgHolder.value);
		String expected = "error: include file 'addIncludePath.h' not found";
		Assert.assertTrue(String.format(
				"Expected contains '%s' but actual is '%s'.", expected,
				msgHolder.value), msgHolder.value.contains(expected));
		state.delete();

		state = new State();
		Assert.assertTrue(state.setOptions("-Itest/include -Ixxxxx"));
		Assert.assertTrue(state.compile("test/addIncludePath.c"));
		Assert.assertEquals(0, state.run());
		state.delete();
	}
}
