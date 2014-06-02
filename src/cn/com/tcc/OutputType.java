package cn.com.tcc;

public enum OutputType {
	/* output will be run in memory (default) */
	MEMORY(0, "memory", "memory"),

	/* executable file */
	EXE(1, "exe", "executable file"),

	/* dynamic library */
	DLL(2, "dll", "dynamic library"),

	/* object file */
	OBJ(3, "obj", "object file"),

	/* only preprocess (used internally) */
	PREPROCESS(4, "preprocess", "preprocess (used internally)");

	private final int type;
	private final String name;
	private final String description;

	private OutputType(int type, String name, String description) {
		this.type = type;
		this.name = name;
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
