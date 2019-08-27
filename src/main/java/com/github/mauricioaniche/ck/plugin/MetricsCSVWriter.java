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
	private String classFileName;
	private String methodFileName;
	private String variableFileName;
	private String fieldFileName;

	public MetricsCSVWriter(String dirName) {
		this.classFileName = resolveFileName(dirName, "class.csv");
		this.methodFileName = resolveFileName(dirName, "method.csv");
		this.variableFileName = resolveFileName(dirName, "variable.csv");
		this.fieldFileName = resolveFileName(dirName, "field.csv");
		try {
			this.resultWriter = new ResultWriter(classFileName, methodFileName, variableFileName, fieldFileName);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getClassFileName() {
		return classFileName;
	}
	public String getFieldFileName() {
		return fieldFileName;
	}
	public String getMethodFileName() {
		return methodFileName;
	}

	public String getVariableFileName() {
		return variableFileName;
	}
	
	private String resolveFileName(String dirName, String fname) {
		return Paths.get(dirName, fname).toString();
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
