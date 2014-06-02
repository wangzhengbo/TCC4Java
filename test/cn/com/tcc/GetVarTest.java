package cn.com.tcc;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jna.NativeLong;

public class GetVarTest extends BaseTest {
	@Test
	public void getVar() throws IOException {
		State state = new State();
		Assert.assertTrue(state.compile("test/getVar.c", "UTF-8"));
		Assert.assertTrue(state.relocateAuto());

		Assert.assertNull(state.getByteVar("cc"));
		Byte c = state.getByteVar("c");
		Assert.assertNotNull(c);
		Assert.assertEquals('T', c.byteValue());

		Assert.assertNull(state.getShortVar("ss"));
		Short s = state.getShortVar("s");
		Assert.assertNotNull(s);
		Assert.assertEquals(5, s.shortValue());

		Assert.assertNull(state.getCharVar("wcwc"));
		Character wc = state.getCharVar("wc");
		Assert.assertNotNull(wc);
		// Assert.assertTrue(
		// String.format("Assert char is '好', but actual is '%s'.",
		// String.valueOf(wc.charValue())), '好' == wc.charValue());

		Assert.assertNull(state.getIntVar("ii"));
		Integer i = state.getIntVar("i");
		Assert.assertNotNull(i);
		Assert.assertEquals(10, i.intValue());

		Assert.assertNull(state.getLongVar("ll"));
		Long l = state.getLongVar("l");
		Assert.assertNotNull(l);
		Assert.assertEquals(20, l.longValue());

		Assert.assertNull(state.getNativeLongVar("ll"));
		NativeLong nl = state.getNativeLongVar("l");
		Assert.assertNotNull(nl);
		Assert.assertEquals(20, nl.longValue());

		Assert.assertNull(state.getStringVar("msgmsg"));
		String msg = state.getStringVar("msg");
		Assert.assertNotNull(msg);
		Assert.assertEquals("Hello, World!", msg);

		String msg2 = state.getByteArrayVarAsString("msg2");
		Assert.assertNotNull(msg2);
		Assert.assertEquals("hello, world!", msg2);

		// TODO:
		String utf8Msg = state.getStringVar("utf8Msg", false);
		Assert.assertNotNull(utf8Msg);
		Assert.assertEquals("你好，世界！", utf8Msg);

		String utf8Msg2 = state.getByteArrayVarAsString("utf8Msg2");
		Assert.assertNotNull(utf8Msg2);
		Assert.assertEquals("你好，世界！", utf8Msg2);

		byte[] byteArray = state.getByteArrayVar("msg2", 4);
		Assert.assertNotNull(byteArray);
		Assert.assertArrayEquals(new byte[] { 'h', 'e', 'l', 'l' }, byteArray);

		// char[] charArray = state.getCharArrayVar("msg", 4);
		// Assert.assertNotNull(charArray);
		// System.out.println(new String(charArray));
		// Assert.assertArrayEquals(new char[] { 'h', 'e', 'l', 'l' },
		// charArray);

		state.delete();
	}
}
