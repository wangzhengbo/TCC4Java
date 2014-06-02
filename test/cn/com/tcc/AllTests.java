package cn.com.tcc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AddFileTest.class, AddIncludePathTest.class,
		AddSysIncludePathTest.class, CompileStringTest.class,
		DefineSymbolTest.class, DeleteTest.class, GetVarTest.class,
		OutputFileTest.class, RunTest.class, SetErrorFuncTest.class,
		SetLibPathTest.class, SetOptionsTest.class, SetOutputTypeTest.class,
		UndefineSymbolTest.class })
public class AllTests {
}
