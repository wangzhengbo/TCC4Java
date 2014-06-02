package cn.com.tcc;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.omg.CORBA.StringHolder;

public class BaseTest {
	protected static final String PROGRAM_ADD = "int add(int a, int b) {return a + b;}";

	@Rule
	public TestName testName = new TestName();

	@BeforeClass
	public static void beforeClass() {
		TCC.init(new File("tcc").getAbsolutePath());
	}

	@Before
	public void setUp() {
		System.out.println(String.format("--------------------Begin test %s",
				testName.getMethodName()));
	}

	@After
	public void tearDown() {
		System.out.println(String.format(
				" --------------------End   test %s%n",
				testName.getMethodName()));
	}

	protected void waitErrorMessage(StringHolder msgHolder) {
		int count = 0;
		while (msgHolder.value == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Assert.fail();
			}
			count++;
			if (count >= 10) {
				break;
			}
		}
	}
}
