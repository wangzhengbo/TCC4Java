package cn.com.tcc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.Callback;
import com.sun.jna.Function;

public class AddFunctionTest extends BaseTest {
	@Test
	public void addFunction() throws IOException {
		State state = new State();

		Assert.assertTrue(state
				.compileString("int add(int a, int b) {return sum(a, b);}"));
		Callback sumFunc = new Callback() {
			@SuppressWarnings("unused")
			public int callback(int a, int b) {
				return a + b + 1;
			}
		};
		Assert.assertTrue(state.addFunction("sum", sumFunc));
		Assert.assertTrue(state.relocateAuto());
		Function addFunc = state.getFunction("add");
		Assert.assertNotNull(addFunc);
		Assert.assertEquals(7, addFunc.invokeInt(new Object[] { 1, 5 }));

		state.delete();
	}
}
