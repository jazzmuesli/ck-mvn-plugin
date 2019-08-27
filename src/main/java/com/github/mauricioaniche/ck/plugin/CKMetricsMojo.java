package com.github.mauricioaniche.ck.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.util.ResultWriter;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.NONE)
public class CKMetricsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;

	public void execute() throws MojoExecutionException {
		for (String dirName : project.getCompileSourceRoots()) {
			processSourceDirectory(dirName);
		}
		for (String dirName : project.getTestCompileSourceRoots()) {
			processSourceDirectory(dirName);
		}
	}

	private void processSourceDirectory(String dirName) {
		try {
			getLog().info("Processing " + dirName);
			if (new File(dirName).exists()) {
				String classFileName = resolveFileName(dirName, "class.csv");
				String methodFileName = resolveFileName(dirName, "method.csv");
				String variableFileName = resolveFileName(dirName, "variable.csv");
				String fieldFileName = resolveFileName(dirName, "field.csv");
				ResultWriter writer = new ResultWriter(classFileName, methodFileName, variableFileName, fieldFileName);

				new CK().calculate(dirName, result -> {
					try {
						writer.printResult(result);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});

				writer.flushAndClose();

			}
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
		}
	}

	private String resolveFileName(String dirName, String fname) {
		return Paths.get(dirName, fname).toString();
	}

}
