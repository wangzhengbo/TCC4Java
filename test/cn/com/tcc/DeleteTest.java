package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;

public class DeleteTest extends BaseTest {
	@Test
	public void delete() {
		State state = new State();
		Assert.assertTrue(state.compileString("int test() { return 0; }"));
		Assert.assertTrue(state.relocateAuto());
		Assert.assertNotNull(state.getFunction("test"));
		Assert.assertNotNull(state.getFunction("test"));
		state.delete();
		try {
			state.getFunction("test");
			Assert.fail();
		} catch (TCCException e) {
			Assert.assertEquals(TCCException.TCC_ERROR_STATE_ALREADY_DELETED,
					e.getErrorCode());
		} catch (Throwable e) {
			Assert.fail();
		}
	}
}
