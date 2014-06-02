package cn.com.tcc;

import org.junit.Assert;
import org.junit.Test;

public class SetOutputTypeTest extends BaseTest {
	@Test
	public void setOutputType() {
		for (OutputType outputType : OutputType.values()) {
			State state = new State();
			Assert.assertTrue(state.setOutputType(outputType));
			state.delete();
		}
	}
}
