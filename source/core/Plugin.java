package core;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import utility.DatedPrintStream;
import utility.Misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Plugin {

	@SuppressWarnings("unused")
	private static int loaded;

	public static PythonInterpreter pythonInterpreter = new PythonInterpreter();

	static {
		Plugin.pythonInterpreter.setOut(new DatedPrintStream(System.out));
		Plugin.pythonInterpreter.setErr(new DatedPrintStream(System.err));
	}

	public static Object callFunc(Class<?> c, String funcName, Object... binds) {
		try {
			final PyObject obj = Plugin.pythonInterpreter.get(funcName);
			if (obj != null && obj instanceof PyFunction) {
				final PyFunction func = (PyFunction) obj;
				final PyObject[] objects = new PyObject[binds.length];
				for (int i = 0; i < binds.length; i++) {
					final Object bind = binds[i];
					objects[i] = Py.java2py(bind);
				}
				return func.__call__(objects).__tojava__(c);
			} else {
				return null;
			}
		} catch (final PyException ex) {
			ex.printStackTrace();
			// FileLog.writeLog("plugin_errors", ex.getMessage());
			return null;
		}
	}

	public static boolean execute(String funcName, Object... binds) {
		try {
			final PyObject obj = Plugin.pythonInterpreter.get(funcName);
			if (obj != null && obj instanceof PyFunction) {
				final PyFunction func = (PyFunction) obj;
				final PyObject[] objects = new PyObject[binds.length];
				for (int i = 0; i < binds.length; i++) {
					final Object bind = binds[i];
					objects[i] = Py.java2py(bind);
				}
				func.__call__(objects);
				return true;
			} else {
				return false;
			}
		} catch (PyException ex) {
			Misc.print("Error1: ");
			ex.printStackTrace();
			ex.getMessage();
			return false;
		}
	}

	public static PyObject getVariable(String variable) {
		try {
			return Plugin.pythonInterpreter.get(variable);
		} catch (final PyException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void load() {
		try {
			Plugin.pythonInterpreter.cleanup();
			Plugin.loaded = 0;
			final File scriptDir = new File(ServerConstants.SCRIPT_DIRECTORY);
			if (scriptDir.isDirectory() && !scriptDir.getName().startsWith(".")) {
				final File[] children = scriptDir.listFiles();
				for (final File child : children) {
					if (child.isFile()) {
						if (child.getName().endsWith(ServerConstants.SCRIPT_FILE_EXTENSION)) {
							pythonInterpreter.execfile(new FileInputStream(child));
							loaded++;
						}
					} else {
						Plugin.recurse(child.getPath());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void recurse(String dir) throws IOException {
		final File scriptDir = new File(dir);
		if (scriptDir.isDirectory() && !scriptDir.getName().startsWith(".")) {
			final File[] children = scriptDir.listFiles();
			for (final File child : children) {
				if (child.isFile()) {
					if (child.getName().endsWith(ServerConstants.SCRIPT_FILE_EXTENSION)) {
						try {
						Plugin.pythonInterpreter.execfile(new FileInputStream(child));
						Plugin.loaded++;
						} catch (Exception e) {
							Misc.printWarning("recurse: " + child.getAbsolutePath());
						}

					}
				} else {
					Plugin.recurse(child.getPath());
				}
			}
		}
	}
}
