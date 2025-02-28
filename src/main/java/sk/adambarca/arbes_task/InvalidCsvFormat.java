package sk.adambarca.arbes_task;

public class InvalidCsvFormat extends RuntimeException {

	public InvalidCsvFormat(String message) {
		super(message);
	}

	public InvalidCsvFormat(String message, Throwable cause) {
		super(message, cause);
	}
}
