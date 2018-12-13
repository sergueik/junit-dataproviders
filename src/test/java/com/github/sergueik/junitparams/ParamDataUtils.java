package com.github.sergueik.junitparams;

/**
 * Selected test scenarios annotated for ExcelParametersProvider junitparams data provider and JSON mapper
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ParamDataUtils {

	// https://stackoverflow.com/questions/16509065/get-rid-of-the-value-for-annotation-attribute-must-be-a-constant-expression-me
	// https://stackoverflow.com/questions/2065937/how-to-supply-value-to-an-annotation-from-a-constant-java?rq=1

	public static final String testDataPath = "file:src/test/resources/data.json";

	// NOTE: the Excel data provider interface parameter values has to be constant
	// declaring final is not enough
	// Trying to initialize final testDataPath with any of the below will fail to
	// compile with [ERROR] /junitparams/FileParamsTest.java:
	// element value must be a constant expression

	// The parameters are resolved at compile time - not at runtime
	// It is possible to use some compile time tools (ant, maven?) to set it todesired value 
	// by e.g. generating ParamDataUtils.java on the fly
	// assuming the value is know before the program is run
	// private static Properties properties = readProperties("src/test/resources/test.properties");
	private static final String badDataPath = String.format("file:///%s",
			(readProperties("src/test/resources/test.properties")).getProperty("testDataPath", "file:src/test/resources/data.ods")
					.replaceAll("\\", "/"));

	// origin:
	// http://www.java2s.com/Code/Java/Development-Class/ReadingandWritingaPropertiesFile.htm
	public static Properties readProperties(String resourceFileName) {

		// Read properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(resourceFileName));
		} catch (IOException e) {
		}
		return properties;
	}
	
	public static final String param() {
		return "file:src/test/resources/data.json";
	}
}
