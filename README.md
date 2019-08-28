# ck-mvn-plugin

Chidamber-Kemerer metrics maven plugin.

This plugin uses https://github.com/mauricioaniche/ck and can be used as:

	git clone https://github.com/mauricioaniche/ck
	cd ck && mvn clean install
	cd ../ck-mvn-plugin
	mvn clean install
	mvn com.github.jazzmuesli:ck-mvn-plugin:metrics

Thereafter you will find class.csv, method.csv, variable.csv, field.csv in every folder with source code (such as src/main/java, src/test/java).

You can use this plugin in command-line mode without modifying pom.xml in any project:

	mvn com.github.jazzmuesli:ck-mvn-plugin:metrics

MetricsWriter can be extended to write into MongoDB or elsewhere.
