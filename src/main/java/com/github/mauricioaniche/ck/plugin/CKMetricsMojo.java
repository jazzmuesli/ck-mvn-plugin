package com.github.mauricioaniche.ck.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		List<String> compileSourceRoots = project.getCompileSourceRoots();
		for (String dirName : extractDirs(compileSourceRoots)) {
			processSourceDirectory(dirName);
		}
		List<String> testCompileSourceRoots = project.getTestCompileSourceRoots();
		for (String dirName : extractDirs(testCompileSourceRoots)) {
			processSourceDirectory(dirName);
		}
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

	public static Collection<String> extractDirs(Collection<String> srcDirs ) {
		Set<String> outDirs = new LinkedHashSet<String>();
		for (String dir: srcDirs) {
			Set<String> parsedDirs = Arrays.stream(dir.split("[,;]")).map(s->s.trim()).collect(Collectors.toSet());
			outDirs.addAll(parsedDirs);
		}
		return outDirs;
	}
}
