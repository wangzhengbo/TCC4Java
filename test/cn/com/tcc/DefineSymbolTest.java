package cn.com.tcc;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.Function;

public class DefineSymbolTest extends BaseTest {
	@Test
	public void defineSymbol() throws IOException {
		State state = new State();

		// symbol 'COUNT' defined
		state.defineSymbol("COUNT", "5");
		Assert.assertTrue(state
				.compileString("#define NAME hello\nint i = 10; int test(int value) {return COUNT + value;}"));
		Assert.assertTrue(state.relocateAuto());
		Function func = state.getFunction("test");
		Assert.assertNotNull(func);
		Assert.assertEquals(6, func.invokeInt(new Object[] { 1 }));
		Assert.assertEquals(7, func.invokeInt(new Object[] { 2 }));
		Assert.assertEquals(10, state.getIntVar("i").intValue());
		state.delete();

		// symbol 'NAME' not defined
		state = new State();
		Assert.assertTrue(state.compile(new File("test/defineSymbol.c")));
		Assert.assertTrue(state.relocateAuto());
		func = state.getFunction("testDefineSymbol");
		Assert.assertNotNull(func);
		Assert.assertEquals(0, func.invokeInt(new Object[] {}));
		state.delete();

		// symbol 'NAME' defined
		state = new State();
		state.defineSymbol("NAME");
		Assert.assertTrue(state.compile(new File("test/defineSymbol.c")));
		Assert.assertTrue(state.relocateAuto());
		func = state.getFunction("testDefineSymbol");
		Assert.assertNotNull(func);
		Assert.assertEquals(1, func.invokeInt(new Object[] {}));
		state.delete();
	}
}
