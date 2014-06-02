package cn.com.tcc;

public class TCCException extends RuntimeException {
	public static final int TCC_ERROR_UNKNOWN = 0;
	public static final int TCC_ERROR_OS_NOT_SUPPORTED = 1;
	public static final int TCC_ERROR_UNABLE_TO_LOAD_TCC = 2;
	public static final int TCC_ERROR_STATE_ALREADY_DELETED = 3;

	private static final long serialVersionUID = -3087164768258622208L;
	private int errorCode = TCC_ERROR_UNKNOWN;

	public TCCException() {
		// Do nothing
	}

	public TCCException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public TCCException(String message) {
		super(message);
	}

	public TCCException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
