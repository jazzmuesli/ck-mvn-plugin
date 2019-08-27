package com.github.mauricioaniche.ck.plugin;



import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;


public class CKMetricsMojoTest
{
    @Rule
    public MojoRule rule = new MojoRule()
    {
        @Override
        protected void before() throws Throwable 
        {
        }

        @Override
        protected void after()
        {
        }
    };
    
    @Test
    public void testSomething()
            throws Exception
    {
        File pom = new File( "./" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        CKMetricsMojo myMojo = ( CKMetricsMojo ) rule.lookupConfiguredMojo( pom, "metrics" );
        assertNotNull( myMojo );
        myMojo.execute();

        MavenProject project = (MavenProject) rule.getVariableValueFromObject( myMojo, "project" );

    }

 
}

