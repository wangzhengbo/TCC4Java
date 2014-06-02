package cn.com.tcc;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.Function;

public class UndefineSymbolTest extends BaseTest {
	@Test
	public void undefineSymbol() throws IOException {
		// symbol 'NAME' not defined
		State state = new State();
		Assert.assertTrue(state.compile(new File("test/undefineSymbol.c")));
		Assert.assertTrue(state.relocateAuto());
		Function func = state.getFunction("testUndefineSymbol");
		Assert.assertNotNull(func);
		Assert.assertEquals(0, func.invokeInt(new Object[] {}));
		state.delete();

		// symbol 'NAME' defined
		state = new State();
		state.defineSymbol("NAME");
		Assert.assertTrue(state.compile(new File("test/undefineSymbol.c")));
		Assert.assertTrue(state.relocateAuto());
		func = state.getFunction("testUndefineSymbol");
		Assert.assertNotNull(func);
		Assert.assertEquals(1, func.invokeInt(new Object[] {}));
		state.delete();

		// symbol 'NAME' not defined
		state = new State();
		state.defineSymbol("NAME");
		state.undefineSymbol("NAME");
		Assert.assertTrue(state.compile(new File("test/undefineSymbol.c")));
		Assert.assertTrue(state.relocateAuto());
		func = state.getFunction("testUndefineSymbol");
		Assert.assertNotNull(func);
		Assert.assertEquals(0, func.invokeInt(new Object[] {}));
		state.delete();
	}
}
