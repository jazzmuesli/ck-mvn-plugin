package com.github.mauricioaniche.ck.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.mauricioaniche.ck.CK;

@Mojo(name = "metrics", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.NONE)
public class CKMetricsMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

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
				MetricsWriter writer = createMetricsWriter(dirName);
				new CK().calculate(dirName, writer);
				writer.finish();
			}
		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
		}
	}

	protected MetricsWriter createMetricsWriter(String dirName) {
		MetricsCSVWriter writer = new MetricsCSVWriter(dirName);
		return writer;
	}

}
