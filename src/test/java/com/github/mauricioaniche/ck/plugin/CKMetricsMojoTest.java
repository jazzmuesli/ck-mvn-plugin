package com.github.mauricioaniche.ck.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;


/**
 * integration test for CKMetricsMojo.
 * It runs the plugin and analyses the created file, 
 * we expect to find a line for this class
 * with at least one public method
 *
 */
public class CKMetricsMojoTest {
	@Rule
	public MojoRule rule = new MojoRule();

	@Test
	public void testPlugin() throws Exception {
		File pom = new File("./");
		assertNotNull(pom);
		assertTrue(pom.exists());

		CKMetricsMojo myMojo = (CKMetricsMojo) rule.lookupConfiguredMojo(pom, "metrics");
		assertNotNull(myMojo);
		myMojo.execute();

		MavenProject project = (MavenProject) rule.getVariableValueFromObject(myMojo, "project");
		List<String> testDirs = project.getTestCompileSourceRoots();
		assertEquals(1, testDirs.size());
		List<CSVRecord> testClassMetrics = getMetrics(testDirs, "class.csv");
		Optional<CSVRecord> thisClassMetrics = testClassMetrics.
				stream().
				filter(p->p.get("class").equals(getClass().getCanonicalName())).
				findFirst();
		assertTrue(thisClassMetrics.isPresent());
		Integer publicMethodsInThisClass = Integer.valueOf(thisClassMetrics.get().get("publicMethods"));
		assertTrue("At least one method is expected, " + publicMethodsInThisClass + " found", publicMethodsInThisClass >= 1);
		Integer classModifiers = Integer.valueOf(thisClassMetrics.get().get("modifiers"));
		assertTrue("This class is expected to be public, but found " + classModifiers, Modifier.isPublic(classModifiers));
		
		List<CSVRecord> testMethodMetrics = getMetrics(testDirs, "method.csv");
		assertTrue("This class is expected to have at least 2 methods, found " + testMethodMetrics.size(), testMethodMetrics.size() >= 2);
		Map<String, Integer> testMethodModifiers = testMethodMetrics.stream().
				collect(Collectors.toMap(
						record -> record.get("method"),
						record -> Integer.valueOf(record.get("modifiers"))));
		System.out.println("testMethodModifiers: " + testMethodModifiers);
		assertTrue("getMetrics should be private", Modifier.isPrivate(testMethodModifiers.get("getMetrics/2[java.util.List<java.lang.String>,java.lang.String]")));
		assertTrue("testPlugins should be public", Modifier.isPublic(testMethodModifiers.get("testPlugin/0")));
	}

	private List<CSVRecord> getMetrics(List<String> testDirs, String fname) throws IOException {
		String fullFileName = testDirs.get(0) + "/" + fname;
		assertTrue("Unexpected " + fname + " filename: " + fullFileName,
				fullFileName.endsWith("src/test/java/" + fname));
		
		File file = new File(fullFileName);
		assertTrue("File " + file + " does not exist", file.exists());
		CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
		List<CSVRecord> allRecords = parser.getRecords();
		return allRecords;
	}

}
