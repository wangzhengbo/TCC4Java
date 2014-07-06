package cn.com.tcc;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.tcc.TCC.ErrorFunction;
import cn.com.tcc.TCC.TCCLibrary;

import com.sun.jna.Callback;
import com.sun.jna.Function;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class State {
	public static final int ERROR_RETURN_VALUE = -1;
	public final Pointer TCC_RELOCATE_AUTO = Pointer.createConstant(1);
	private final TCCLibrary tcc;
	private final Pointer tccState;
	private boolean deleted = false;
	@SuppressWarnings("unused")
	private Pointer errorOpaque = null;
	@SuppressWarnings("unused")
	private Callback errorOpaqueCallback = null;
	@SuppressWarnings("unused")
	private ErrorFunction errorFunc = null;

	private static final Logger logger = LoggerFactory.getLogger(State.class);

	public State() {
		tcc = TCC.getTCC();
		tccState = newState();

		// set lib path for non windows system
		setLibPath(TCC.getLibPath());

		// set lib path for windows system
		addLibraryPath(TCC.getLibPath());

		// add system include path
		addSysIncludePath(new File(TCC.getIncludePath()));
		addSysIncludePath(new File(TCC.tccPath, TCC.OS_COMMON + "/include"));
		addSysIncludePath(new File(TCC.getIncludePath(TCC.OS_COMMON)));
	}

	private void addSysIncludePath(File sysIncludePath) {
		if (sysIncludePath.exists() && sysIncludePath.isDirectory()) {
			addSysIncludePath(sysIncludePath.getAbsolutePath());
			File[] files = sysIncludePath.listFiles();
			if (files != null) {
				for (File file : files) {
					addSysIncludePath(file);
				}
			}
		}
	}

	/* create a new TCC compilation context */
	private Pointer newState() {
		return tcc.tcc_new();
	}

	/* free a TCC compilation context */
	public void delete() {
		logger.debug("Delete state.");
		if (!deleted) {
			tcc.tcc_delete(tccState);
			deleted = true;
		}
	}

	/* set CONFIG_TCCDIR at runtime */
	public void setLibPath(String libPath) {
		logger.debug(String.format("Set tcc lib path to %s.", libPath));
		checkStatus();
		tcc.tcc_set_lib_path(tccState, libPath);
	}

	/**
	 * set error/warning display callback
	 * 
	 * @param errorFunc
	 */
	public void setErrorFunc(ErrorFunction errorFunc) {
		setErrorFunc((Callback) null, errorFunc);
	}

	/**
	 * set error/warning display callback
	 * 
	 * @param errorOpaque
	 * @param errorFunc
	 */
	public void setErrorFunc(Pointer errorOpaque, ErrorFunction errorFunc) {
		logger.debug("Set error function.");
		checkStatus();
		// prevents the JVM garbage collection it
		this.errorOpaque = errorOpaque;
		this.errorFunc = errorFunc;
		tcc.tcc_set_error_func(tccState, errorOpaque, errorFunc);
	}

	/**
	 * set error/warning display callback
	 * 
	 * @param errorOpaque
	 * @param errorFunc
	 */
	public void setErrorFunc(Callback errorOpaque, ErrorFunction errorFunc) {
		logger.debug("Set error function.");
		checkStatus();
		// prevents the JVM garbage collection it
		this.errorOpaqueCallback = errorOpaque;
		this.errorFunc = errorFunc;
		tcc.tcc_set_error_func(tccState, errorOpaque, errorFunc);
	}

	/**
	 * set options as from command line (multiple supported)
	 * 
	 * @param tccState
	 * @param options
	 * @return
	 */
	public boolean setOptions(String options) {
		logger.debug(String.format("Set options %s.", options));
		checkStatus();
		return (tcc.tcc_set_options(tccState, options) != ERROR_RETURN_VALUE);
	}

	/* -------------------------- preprocessor -------------------------- */
	/**
	 * add include path
	 * 
	 * @param includePath
	 * @return
	 */
	public boolean addIncludePath(String includePath) {
		logger.debug(String.format("Add include path %s.", includePath));
		checkStatus();
		return (tcc.tcc_add_include_path(tccState,
				new File(includePath).getAbsolutePath()) != ERROR_RETURN_VALUE);
	}

	/**
	 * add in system include path
	 * 
	 * @param includePath
	 * @return
	 */
	public boolean addSysIncludePath(String includePath) {
		logger.debug(String.format("Add system include path %s.", includePath));
		checkStatus();
		return (tcc.tcc_add_sysinclude_path(tccState,
				new File(includePath).getAbsolutePath()) != ERROR_RETURN_VALUE);
	}

	/**
	 * define preprocessor symbol 'sym'.
	 * 
	 * @param sym
	 */
	public void defineSymbol(String sym) {
		logger.debug(String.format("Define symbol %s.", sym));
		defineSymbol(sym, null);
	}

	/**
	 * define preprocessor symbol 'sym'. Can put optional value
	 * 
	 * @param sym
	 * @param val
	 */
	public void defineSymbol(String sym, String val) {
		if ((val == null) || (val.length() == 0)) {
			logger.debug(String.format("Define symbol %s.", sym));
		} else {
			logger.debug(String.format("Define symbol %s=%s.", sym, val));
		}
		checkStatus();
		tcc.tcc_define_symbol(tccState, sym, val);
	}

	/**
	 * undefine preprocess symbol 'sym'
	 * 
	 * @param sym
	 */
	public void undefineSymbol(String sym) {
		logger.debug(String.format("Undefine symbol %s.", sym));
		checkStatus();
		tcc.tcc_undefine_symbol(tccState, sym);
	}

	/* -------------------------- preprocessor -------------------------- */

	/* -------------------------- compiling -------------------------- */
	/**
	 * add a file (C file, dll, object, library, ld script). Return false if
	 * error.
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean addFile(String filePath) {
		logger.debug(String.format("Add file %s.", filePath));
		checkStatus();
		return (tcc
				.tcc_add_file(tccState, new File(filePath).getAbsolutePath()) != ERROR_RETURN_VALUE);
	}

	/**
	 * add a file (C file, dll, object, library, ld script). Return false if
	 * error.
	 * 
	 * @param file
	 * @return
	 */
	public boolean addFile(File file) {
		return addFile(file.getAbsolutePath());
	}

	/**
	 * compile a file containing a C source. Return false if error.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public boolean compile(String filePath) throws IOException {
		return compile(filePath, null);
	}

	/**
	 * compile a file containing a C source. Return false if error.
	 * 
	 * @param filePath
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public boolean compile(String filePath, String encoding) throws IOException {
		return compile(new File(filePath), encoding);
	}

	/**
	 * compile a file containing a C source. Return false if error.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public boolean compile(File file) throws IOException {
		return compile(file, null);
	}

	/**
	 * compile a file containing a C source. Return false if error.
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public boolean compile(File file, String encoding) throws IOException {
		if ((encoding == null) || (encoding.trim().length() == 0)) {
			logger.debug(String.format("Compile file %s.",
					file.getAbsolutePath()));
			return compileString_(FileUtils.readFileToString(file));
		}

		logger.debug(String.format("Compile file %s with encoding %s.",
				file.getAbsolutePath(), encoding));
		return compileString_(FileUtils.readFileToString(file, encoding));
	}

	/**
	 * compile a string containing a C source. Return false if error.
	 * 
	 * @param buf
	 * @return
	 */
	public boolean compileString(String buf) {
		logger.debug(String.format("Compile string %s.", buf));
		return compileString_(buf);
	}

	private boolean compileString_(String buf) {
		checkStatus();
		return (tcc.tcc_compile_string(tccState, buf) != ERROR_RETURN_VALUE);
	}

	/* -------------------------- compiling -------------------------- */

	/* -------------------------- linking commands -------------------------- */
	public boolean setOutputType(OutputType outputType) {
		logger.debug(String.format("Set output type to %s.",
				outputType.getDescription()));
		checkStatus();
		return (tcc.tcc_set_output_type(tccState, outputType.getType()) != ERROR_RETURN_VALUE);
	}

	/**
	 * equivalent to -LlibraryPath option
	 * 
	 * @param libraryPath
	 * @return
	 */
	public boolean addLibraryPath(String libraryPath) {
		logger.debug(String.format("Add library path %s.", libraryPath));
		checkStatus();
		return (tcc.tcc_add_library_path(tccState, libraryPath) != ERROR_RETURN_VALUE);
	}

	/**
	 * the library name is the same as the argument of the '-l' option
	 * 
	 * @param libraryName
	 * @return
	 */
	public boolean addLibrary(String libraryName) {
		logger.debug(String.format("Add library %s.", libraryName));
		checkStatus();
		return (tcc.tcc_add_library(tccState, libraryName) != ERROR_RETURN_VALUE);
	}

	/**
	 * add a symbol to the compiled program
	 * 
	 * @param name
	 * @param val
	 * @return
	 */
	public boolean addSymbol(String name, Pointer val) {
		logger.debug(String.format("Add sysmbol %s.", name));
		checkStatus();
		return (tcc.tcc_add_symbol(tccState, name, val) != ERROR_RETURN_VALUE);
	}

	/**
	 * add a function to the compiled program
	 * 
	 * @param name
	 * @param func
	 * @return
	 */
	public boolean addFunction(String name, Callback func) {
		logger.debug(String.format("Add function %s.", name));
		checkStatus();
		return (tcc.tcc_add_symbol(tccState, name, func) != ERROR_RETURN_VALUE);
	}

	/**
	 * output an executable, library or object file. DO NOT call tcc_relocate()
	 * before.
	 * 
	 * @param file
	 * @return
	 */
	public boolean outputFile(File file) {
		return outputFile(file.getAbsolutePath());
	}

	/**
	 * output an executable, library or object file. DO NOT call tcc_relocate()
	 * before.
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean outputFile(String filePath) {
		logger.debug(String.format("Output to file %s.", filePath));
		checkStatus();
		return (tcc.tcc_output_file(tccState, filePath) != ERROR_RETURN_VALUE);
	}

	/**
	 * link and run main() function and return its value. DO NOT call
	 * tcc_relocate() before.
	 * 
	 * @return
	 */
	public int run() {
		return run(new Object[0]);
	}

	/**
	 * link and run main() function and return its value. DO NOT call
	 * tcc_relocate() before.
	 * 
	 * @param argv
	 * @return
	 */
	public int run(Object... argv) {
		checkStatus();
		String[] strArgv = new String[(argv == null) ? 0 : argv.length];
		if (argv != null) {
			for (int i = 0; i < argv.length; i++) {
				strArgv[i] = (argv[i] == null) ? "" : argv[i].toString();
			}
		}
		return tcc.tcc_run(tccState, strArgv.length, strArgv);
	}

	/**
	 * do all relocations (needed before using tcc_get_symbol())
	 * 
	 * possible values for 'ptr':<br/>
	 * - TCC_RELOCATE_AUTO : Allocate and manage memory internally<br/>
	 * - NULL : return required memory size for the step below<br/>
	 * - memory address : copy code to memory passed by the caller<br/>
	 * returns false if error.
	 */
	public int relocate(Pointer ptr) {
		logger.debug("Relocate.");
		checkStatus();
		return tcc.tcc_relocate(tccState, ptr);
	}

	/**
	 * Allocate and manage memory internally.
	 * 
	 * returns false if error.
	 */
	public boolean relocateAuto() {
		logger.debug("Auto relocate.");
		return (relocate(TCC_RELOCATE_AUTO) != ERROR_RETURN_VALUE);
	}

	/**
	 * return symbol value or null if not found
	 * 
	 * @param name
	 * @return
	 */
	public Pointer getSymbol(String name) {
		logger.debug(String.format("Get symbol %s.", name));
		checkStatus();
		return tcc.tcc_get_symbol(tccState, name);
	}

	/**
	 * return function or null if not found
	 * 
	 * @param funcName
	 * @return
	 */
	public Function getFunction(String funcName) {
		Pointer symbol = getSymbol(funcName);
		return (symbol == null) ? null : Function.getFunction(symbol);
	}

	/**
	 * return byte value (char in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Byte getByteVar(String varName) {
		Pointer symbol = getSymbol(varName);
		if (symbol == null) {
			return null;
		}
		return symbol.getByte(0);
	}

	/**
	 * return byte[] value (char[] in C) or null if not found
	 * 
	 * @param varName
	 * @param arraySize
	 * @return
	 */
	public byte[] getByteArrayVar(String varName, int arraySize) {
		Pointer symbol = getSymbol(varName);
		if (symbol == null) {
			return null;
		}
		return symbol.getByteArray(0, arraySize);
	}

	/**
	 * return char value (wchar_t in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Character getCharVar(String varName) {
		Pointer symbol = getSymbol(varName);
		if (symbol == null) {
			return null;
		}
		return symbol.getChar(0);
	}

	/**
	 * return short value (short in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Short getShortVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getShort(0);
	}

	/**
	 * return int value (int in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Integer getIntVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getInt(0);
	}

	/**
	 * return boolean value (int in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Boolean getBooleanVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : (symbol.getInt(0) != 0);
	}

	/**
	 * return long value (long long in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Long getLongVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getLong(0);
	}

	/**
	 * return native long value (long in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public NativeLong getNativeLongVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getNativeLong(0);
	}

	/**
	 * return float value (float in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Float getFloatVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getFloat(0);
	}

	/**
	 * return double value (double in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public Double getDoubleVar(String varName) {
		Pointer symbol = getSymbol(varName);
		return (symbol == null) ? null : symbol.getDouble(0);
	}

	/**
	 * return String value (char* in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public String getStringVar(String varName) {
		return getStringVar(varName, false);
	}

	/**
	 * return String value (char* in C) or null if not found
	 * 
	 * @param varName
	 * @param wide
	 * @return
	 */
	public String getStringVar(String varName, boolean wide) {
		Pointer symbol = getSymbol(varName);
		if (symbol == null) {
			return null;
		}
		// for char* type
		return symbol.getPointer(0).getString(0, wide);
	}

	/**
	 * return String value (char[] in C) or null if not found
	 * 
	 * @param varName
	 * @return
	 */
	public String getByteArrayVarAsString(String varName) {
		return getByteArrayVarAsString(varName, false);
	}

	/**
	 * return String value (char[] in C) or null if not found
	 * 
	 * @param varName
	 * @param wide
	 * @return
	 */
	public String getByteArrayVarAsString(String varName, boolean wide) {
		Pointer symbol = getSymbol(varName);
		if (symbol == null) {
			return null;
		}
		// for char[] type
		return symbol.getString(0, wide);
	}

	/* -------------------------- linking commands -------------------------- */

	public boolean isDeleted() {
		return deleted;
	}

	private void checkStatus() {
		if (deleted) {
			throw new TCCException(TCCException.TCC_ERROR_STATE_ALREADY_DELETED);
		}
	}
}
