package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.StringHolder;

import cn.com.tcc.TCC.ErrorFunction;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.Pointer;

public class SetErrorFuncTest extends BaseTest {
	@Test
	public void setErrorFunc() {
		State state = new State();
		final ObjHolder opaqueHolder = new ObjHolder();
		final StringHolder msgHolder = new StringHolder();
		state.setErrorFunc(new ErrorFunction() {
			@Override
			public void callback(Pointer opaque, String msg) {
				opaqueHolder.value = opaque;
				msgHolder.value = msg;
			}
		});
		Assert.assertFalse(state
				.compileString("#include <xxxx.h>\nint main() { return 0; }"));
		waitErrorMessage(msgHolder);
		Assert.assertNull(opaqueHolder.value);
		Assert.assertNotNull(msgHolder.value);
		String expected = "error: include file 'xxxx.h' not found";
		Assert.assertTrue(String.format(
				"Expected contains '%s' but actual is '%s'.", expected,
				msgHolder.value), msgHolder.value.contains(expected));
		state.delete();

		state = new State();
		opaqueHolder.value = null;
		msgHolder.value = null;
		final IntHolder intHolder = new IntHolder();
		state.setErrorFunc(new Callback() {
			@SuppressWarnings("unused")
			public void callback() {
				intHolder.value = 10;
			}
		}, new ErrorFunction() {
			@Override
			public void callback(Pointer opaque, String msg) {
				opaqueHolder.value = opaque;
				msgHolder.value = msg;
			}
		});
		Assert.assertFalse(state
				.compileString("#include <xxxx.h>\nint main() { return 0; }"));
		waitErrorMessage(msgHolder);
		Assert.assertNotNull(opaqueHolder.value);
		Assert.assertNotNull(msgHolder.value);
		expected = "error: include file 'xxxx.h' not found";
		Assert.assertTrue(String.format(
				"Expected contains '%s' but actual is '%s'.", expected,
				msgHolder.value), msgHolder.value.contains(expected));
		Function func = Function.getFunction((Pointer) opaqueHolder.value);
		func.invoke(new Object[] {});
		Assert.assertEquals(10, intHolder.value);
		state.delete();
	}

	private static class ObjHolder {
		public Object value = null;
	}
}
