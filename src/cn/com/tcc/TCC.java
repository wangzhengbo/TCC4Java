package cn.com.tcc;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

public final class TCC {
	protected static final String OS_COMMON = "common";

	/* TCC library path */
	protected static String tccPath = "";

	/* TCC library name */
	private static final String DLL_LIB_NAME = "tcc";

	private static final String ENV_CONFIG_TCC_DIR = "CONFIG_TCCDIR";
	private static final String ENV_CONFIG_OS = "CONFIG_OS";

	private static final String OS_NAME_AIX = "AIX";
	private static final String OS_NAME_GNU = "gnu";
	private static final String OS_NAME_FREEBSD = "FreeBSD";
	private static final String OS_NAME_KFREEBSD = "kFreeBSD";
	private static final String OS_NAME_NETBSD = "NetBSD";
	private static final String OS_NAME_DRAGONFLYBSD = "DragonFly";
	private static final String OS_NAME_MIDNIGHTBSD = "MidnightBSD";

	private static final int AIX = 7;
	private static final int ANDROID = 8;
	private static final int GNU = 9;
	private static final int KFREEBSD = 10;
	private static final int NETBSD = 11;
	private static final int DRAGONFLYBSD = 1000;

	private static final Logger logger = LoggerFactory.getLogger(TCC.class);

	static {
		if (OS_NAME_MIDNIGHTBSD.equalsIgnoreCase(System.getProperty("os.name"))) {
			System.setProperty("os.name", OS_NAME_FREEBSD);
		}
		System.setProperty("jna.library.path",
				System.getProperty("java.io.tmpdir"));
		init(System.getenv(ENV_CONFIG_TCC_DIR));
	}

	public static void init(String tccPath) {
		if ((tccPath != null) && (tccPath.trim().length() > 0)
				&& !tccPath.equals(TCC.tccPath)) {
			TCC.tccPath = tccPath;
			logger.info(String.format("Set tccPath to %s.", tccPath));
		}
	}

	private static final ThreadLocal<TCCLibrary> threadLocalTCC = new ThreadLocal<TCCLibrary>() {
		@Override
		protected TCCLibrary initialValue() {
			TCCLibrary tcc = null;

			// Initialize State
			try {
				tcc = loadNativeLibrary();

				logger.info(String.format("%s initialized for thread %s.",
						TCC.class.getSimpleName(), Thread.currentThread()
								.getName()));
			} catch (Throwable e) {
				logger.warn(String.format(
						"Unable to initialize %s for thread %s.", TCC.class
								.getSimpleName(), Thread.currentThread()
								.getName()), e);
			}

			return tcc;
		}
	};

	private TCC() {
		// Do nothing
	}

	protected static TCCLibrary getTCC() throws TCCException {
		if (getOS() == null) {
			throw new TCCException(TCCException.TCC_ERROR_OS_NOT_SUPPORTED,
					String.format("Unsupported OS: %s-%s.",
							System.getProperty("os.name"),
							System.getProperty("os.arch")));
		}

		TCCLibrary tccLibrary = threadLocalTCC.get();
		if (tccLibrary == null) {
			throw new TCCException(TCCException.TCC_ERROR_UNABLE_TO_LOAD_TCC,
					"Unable to load tcc.");
		}

		return tccLibrary;
	}

	protected static String getIncludePath() {
		return getIncludePath(getOSName());
	}

	protected static String getIncludePath(String osName) {
		if (!OS_COMMON.equals(osName)) {
			File includeFile = new File(getSharedLibPath(), "include");
			if (!includeFile.exists() || !includeFile.isDirectory()) {
				osName = OS_COMMON;
			}
		}
		return new File(tccPath, getSharedLibPath(osName) + "/include")
				.getAbsolutePath();
	}

	protected static String getLibPath() {
		String osName = getOSName();
		if (!OS_COMMON.equals(osName)) {
			File includeFile = new File(getSharedLibPath(), "lib");
			if (!includeFile.exists() || !includeFile.isDirectory()) {
				osName = OS_COMMON;
			}
		}
		return new File(tccPath, getSharedLibPath(osName) + "/lib")
				.getAbsolutePath();
	}

	protected static String getSharedLibPath() {
		return new File(tccPath, getSharedLibPath(getOSName()))
				.getAbsolutePath();
	}

	protected static String getSharedLibPath(String osName) {
		OS os = getOS(osName);
		return getNativeLibraryResourcePrefix(getOS(osName),
				System.getProperty("os.arch"),
				(os == null) ? System.getProperty("os.name") : os.getName());
	}

	protected static String getOSName() {
		boolean validOS = false;
		String os = System.getenv(ENV_CONFIG_OS);
		if (os == null) {
			os = System.getProperty(ENV_CONFIG_OS);
		}
		if (os != null) {
			for (OS item : OS.values()) {
				if (item.getName().equalsIgnoreCase(os)) {
					os = item.getName();
					validOS = true;
					break;
				}
			}
		}

		if (!validOS) {
			os = null;
			for (OS item : OS.values()) {
				if (item.getType() == Platform.getOSType()) {
					os = item.getName();
					break;
				}
			}
			if (os == null) {
				String osName = System.getProperty("os.name");
				if (osName.startsWith(OS_NAME_AIX)) {
					os = OS.AIX.getName();
				} else if (OS_NAME_GNU.equalsIgnoreCase(osName)) {
					os = OS.GNU.getName();
				} else if (OS_NAME_KFREEBSD.equalsIgnoreCase(osName)) {
					os = OS.KFREEBSD.getName();
				} else if (OS_NAME_NETBSD.equalsIgnoreCase(osName)) {
					os = OS.NETBSD.getName();
				} else if (OS_NAME_DRAGONFLYBSD.equals(osName)) {
					os = OS.DRAGONFLYBSD.getName();
				} else {
					os = OS_COMMON;
				}
			}
		}

		return os;
	}

	protected static OS getOS() {
		return getOS(getOSName());
	}

	protected static OS getOS(String osName) {
		OS os = null;
		if (!OS_COMMON.equals(osName)) {
			for (OS item : OS.values()) {
				if (item.getName().equalsIgnoreCase(osName)) {
					os = item;
					break;
				}
			}
		}
		return os;
	}

	/**
	 * Generate a canonical String prefix based on the given OS type/arch/name.
	 * 
	 * @param osType
	 *            from {@link #getOS(String)}
	 * @param arch
	 *            from <code>os.arch</code> System property
	 * @param name
	 *            from <code>os.name</code> System property
	 */
	protected static String getNativeLibraryResourcePrefix(OS osType,
			String arch, String name) {
		String osPrefix;
		arch = arch.toLowerCase().trim();
		arch = arch.toLowerCase().trim();
		if ("powerpc".equals(arch)) {
			arch = "ppc";
		} else if ("powerpc64".equals(arch)) {
			arch = "ppc64";
		} else if ("i386".equals(arch)) {
			arch = "x86";
		} else if ("x86_64".equals(arch) || "amd64".equals(arch)) {
			arch = "x86-64";
		}
		if (osType == null) {
			osPrefix = OS_COMMON + "/" + arch;
		} else {
			switch (osType) {
			case MAC:
				osPrefix = osType.getName().toLowerCase();
				break;
			case ANDROID:
				if (arch.startsWith("arm")) {
					arch = "arm";
				}
			case WINDOWS:
			case WINDOWSCE:
			case LINUX:
			case SOLARIS:
			case FREEBSD:
			case OPENBSD:
			case NETBSD:
			case KFREEBSD:
				osPrefix = osType.getName().toLowerCase() + "/" + arch;
				break;
			default:
				osPrefix = name.toLowerCase();
				int space = osPrefix.indexOf(" ");
				if (space != -1) {
					osPrefix = osPrefix.substring(0, space);
				}
				osPrefix += "/" + arch;
				break;
			}
		}
		return osPrefix;
	}

	/**
	 * Unpacking and loading the library into the Java Virtual Machine.
	 */
	private static TCCLibrary loadNativeLibrary() {
		try {
			// Get what the system "thinks" the library name should be.
			String libName = DLL_LIB_NAME;
			String libNativeName = System.mapLibraryName(libName);

			// Slice up the library name.
			int index = libNativeName.lastIndexOf('.');
			String libNativePrefix = libNativeName.substring(0, index);
			String libNativeSuffix = libNativeName.substring(index);

			// Create the temp file for this instance of the library.
			File libFile = File
					.createTempFile(libNativePrefix, libNativeSuffix);
			libFile.deleteOnExit();

			// Copy libtcc.dll or libtcc_x64.dll library to the temp file.
			String sharedLibPath = getSharedLibPath();
			logger.trace(String.format("Shared lib path for tcc is %s.",
					sharedLibPath));
			copyInputStreamToFile(new FileInputStream(new File(sharedLibPath,
					libNativeName)), libFile);

			Native.setProtected(true);
			libName = libFile.getName().substring(
					libNativePrefix.length() - libName.length(),
					libFile.getName().length() - libNativeSuffix.length());
			return (TCCLibrary) Native.loadLibrary(libName, TCCLibrary.class);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static void closeQuietly(final Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignore exception
			}
		}
	}

	private static void copyInputStreamToFile(final InputStream input,
			final File file) throws IOException {
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
			int n = 0;
			byte[] buffer = new byte[4 * 1024];
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
			}
		} finally {
			closeQuietly(output);
			closeQuietly(input);
		}
	}

	protected static interface TCCLibrary extends Library {
		/* create a new TCC compilation context */
		public Pointer tcc_new();

		/* free a TCC compilation context */
		public void tcc_delete(Pointer tccState);

		/* set CONFIG_TCCDIR at runtime */
		public void tcc_set_lib_path(Pointer tccState, String path);

		/* set error/warning display callback */
		public void tcc_set_error_func(Pointer tccState, Pointer error_opaque,
				Callback error_func);

		public void tcc_set_error_func(Pointer tccState, Callback error_opaque,
				Callback error_func);

		/* set options as from command line (multiple supported) */
		public int tcc_set_options(Pointer tccState, String str);

		/*****************************/
		/* preprocessor */

		/* add include path */
		public int tcc_add_include_path(Pointer tccState, String pathname);

		/* add in system include path */
		public int tcc_add_sysinclude_path(Pointer tccState, String pathname);

		/* define preprocessor symbol 'sym'. Can put optional value */
		public void tcc_define_symbol(Pointer tccState, String sym, String val);

		/* undefine preprocess symbol 'sym' */
		public void tcc_undefine_symbol(Pointer tccState, String sym);

		/*****************************/
		/* compiling */

		/*
		 * add a file (C file, dll, object, library, ld script). Return -1 if
		 * error.
		 */
		public int tcc_add_file(Pointer tccState, String filename);

		/* compile a string containing a C source. Return -1 if error. */
		public int tcc_compile_string(Pointer tccState, String buf);

		/*****************************/
		/* linking commands */

		/* set output type. MUST BE CALLED before any compilation */
		public int tcc_set_output_type(Pointer tccState, int type);

		/* equivalent to -Lpath option */
		public int tcc_add_library_path(Pointer tccState, String pathname);

		/* the library name is the same as the argument of the '-l' option */
		public int tcc_add_library(Pointer tccState, String libraryname);

		/* add a symbol to the compiled program */
		public int tcc_add_symbol(Pointer tccState, String name, Pointer val);

		public int tcc_add_symbol(Pointer tccState, String name, Callback val);

		/*
		 * output an executable, library or object file. DO NOT call
		 * tcc_relocate() before.
		 */
		public int tcc_output_file(Pointer tccState, String filename);

		/*
		 * link and run main() function and return its value. DO NOT call
		 * tcc_relocate() before.
		 */
		public int tcc_run(Pointer tccState, int argc, String[] argv);

		/**
		 * do all relocations (needed before using tcc_get_symbol())
		 * 
		 * possible values for 'ptr':<br/>
		 * - TCC_RELOCATE_AUTO : Allocate and manage memory internally<br/>
		 * - NULL : return required memory size for the step below<br/>
		 * - memory address : copy code to memory passed by the caller<br/>
		 * returns -1 if error.
		 */
		public int tcc_relocate(Pointer tccState, Pointer ptr);

		/* return symbol value or NULL if not found */
		public Pointer tcc_get_symbol(Pointer tccState, String name);

	}

	public static interface ErrorFunction extends Callback {
		public void callback(Pointer opaque, String msg);
	}

	private static enum OS {
		MAC(Platform.MAC, "darwin"), LINUX(Platform.LINUX, "linux"), WINDOWS(
				Platform.WINDOWS, "win32"), SOLARIS(Platform.SOLARIS, "sunos"), FREEBSD(
				Platform.FREEBSD, "freebsd"), OPENBSD(Platform.OPENBSD,
				"openbsd"), WINDOWSCE(Platform.WINDOWSCE, "w32ce"), AIX(
				TCC.AIX, OS_NAME_AIX), ANDROID(TCC.ANDROID, "android"), GNU(
				TCC.GNU, OS_NAME_GNU), KFREEBSD(TCC.KFREEBSD, OS_NAME_KFREEBSD), NETBSD(
				TCC.NETBSD, OS_NAME_NETBSD), DRAGONFLYBSD(TCC.DRAGONFLYBSD,
				OS_NAME_DRAGONFLYBSD);

		private final int type;
		private final String name;

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		private OS(int type, String name) {
			this.type = type;
			this.name = name;
		}
	}
}
