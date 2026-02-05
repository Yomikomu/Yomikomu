package exception;

import static java.lang.System.err;

public class UhOhPythonDied extends RuntimeException {
    public UhOhPythonDied(String message, Throwable err) {
        super(message, err);

    }
}
