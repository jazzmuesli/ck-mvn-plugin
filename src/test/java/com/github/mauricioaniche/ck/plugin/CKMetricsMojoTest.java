package com.github.mauricioaniche.ck.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
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
		String classFileName = testDirs.get(0) + "/class.csv";
		assertTrue("Unexpected class.csv filename: " + classFileName,
				classFileName.endsWith("src/test/java/class.csv"));
		
		File file = new File(classFileName);
		assertTrue("File " + file + " does not exist", file.exists());
		CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT.withFirstRecordAsHeader());
		List<CSVRecord> allRecords = parser.getRecords();
		List<CSVRecord> thisClassMetrics = allRecords.
				stream().
				filter(p->p.get("class").equals(getClass().getCanonicalName())).
				collect(Collectors.toList());
		assertEquals(1, thisClassMetrics.size());
		Integer publicMethodsInThisClass = Integer.valueOf(thisClassMetrics.get(0).get("publicMethods"));
		assertTrue("At least one method is expected, " + publicMethodsInThisClass + " found", publicMethodsInThisClass >= 1);
	}

}
