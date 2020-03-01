package com.github.mauricioaniche.ck.plugin;

import java.io.File;
import java.util.Collection;

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
	public MavenProject project;

	public void execute() throws MojoExecutionException {
		for (String dirName : getCompileSourceDirs()) {
			processSourceDirectory(dirName);
		}
		for (String dirName : getTestCompileSourceDirs()) {
			processSourceDirectory(dirName);
		}
	}

	protected Collection<String> getTestCompileSourceDirs() {
		return Helper.extractDirs(project.getTestCompileSourceRoots());
	}

	protected Collection<String> getCompileSourceDirs() {
		return Helper.extractDirs(project.getCompileSourceRoots());
	}

	protected void processSourceDirectory(String dirName) {
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
