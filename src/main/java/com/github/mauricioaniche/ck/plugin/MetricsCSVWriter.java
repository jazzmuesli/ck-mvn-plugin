package com.github.mauricioaniche.ck.plugin;

import java.io.IOException;
import java.nio.file.Paths;

import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.util.ResultWriter;

/**
 * Write metrics as CSV
 *
 */
public class MetricsCSVWriter implements MetricsWriter{

	private ResultWriter resultWriter;

	protected ResultWriter createResultWriter(String dirName) throws IOException {
		String classFileName = resolveFileName(dirName, "class.csv");
		String methodFileName = resolveFileName(dirName, "method.csv");
		String variableFileName = resolveFileName(dirName, "variable.csv");
		String fieldFileName = resolveFileName(dirName, "field.csv");
		ResultWriter writer = new ResultWriter(classFileName, methodFileName, variableFileName, fieldFileName);
		return writer;
	}

	private String resolveFileName(String dirName, String fname) {
		return Paths.get(dirName, fname).toString();
	}
	
	@Override
	public void init(String dirName) {
		try {
			this.resultWriter = createResultWriter(dirName);
		} catch (IOException e) {
			throw new RuntimeException("Cannot initialise for directory " + dirName + " due to " + e.getMessage(), e);
		}
	}

	@Override
	public void notify(CKClassResult result) {
		try {
			resultWriter.printResult(result);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}		
	}

	@Override
	public void finish() {
		try {
			resultWriter.flushAndClose();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}		
	}

}
