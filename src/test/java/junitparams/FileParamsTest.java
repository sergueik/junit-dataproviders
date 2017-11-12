package junitparams;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

import junitparams.custom.CustomParameters;
import junitparams.custom.ParametersProvider;
import junitparams.custom.FileParametersProvider;
import junitparams.mappers.CsvWithHeaderMapper;
import junitparams.mappers.DataMapper;

import org.json.JSONArray;
import org.json.JSONObject;

@RunWith(JUnitParamsRunner.class)
public class FileParamsTest {

	@Ignore
	@Test
	@FileParameters("src/test/resources/test.csv")
	public void loadParamsFromFileWithIdentityMapper(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters(value = "src/test/resources/test.csv", mapper = PersonMapper.class)
	public void loadParamsFromFileWithCustomMapper(Person person) {
		assertThat(person.getAge()).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters("classpath:test.csv")
	public void loadParamsFromFileAtClasspath(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Ignore
	@Test
	@FileParameters("file:src/test/resources/test.csv")
	public void loadParamsFromFileAtFilesystem(int age, String name) {
		assertThat(age).isGreaterThan(0);
	}

	@Test
	@FileParameters(value = "classpath:data.json", mapper = JSONMapper.class)
	public void loadParamsFromJSONEmbedded(String keyword, double count) {
		assertThat((int) count).isGreaterThan(0);
		assertTrue(keyword.matches("(?:junit|testng|spock)"));
	}

	@Test
	@FileParameters(value = "file:src/test/resources/data.json", mapper = JSONMapper.class )
	public void loadParamsFromJSONFile(String keyword, double count) {
		assertThat(keyword, notNullValue());
		System.err
		.println(String.format("keyword: %s\tcount=%.2f", keyword, count));
	}

	@Ignore
	@Test
	@FileParameters(value = "classpath:with_header.csv", mapper = CsvWithHeaderMapper.class)
	public void csvWithHeader(int id, String name) {
		assertThat(id).isGreaterThan(0);
	}

	/*
		@Test
		@FileParameters(value = "classpath:with_special_chars.csv", encoding = "UTF-8")
		public void loadParamWithCorrectEncoding(String value) {
			assertThat(value).isEqualTo("åäöÅÄÖ");
		}
	
		@Test
		@FileParameters(value = "classpath:with_special_chars.csv", encoding = "ISO-8859-1")
		public void loadParamWithWrongEncoding(String value) {
			assertThat(value).isNotEqualTo("åäöÅÄÖ");
		}
	
		@Test
		@FileParameters(value = "src/test/resources/ISO-8859-1.csv", encoding = "iso-8859-1")
		public void loadFromAnsi(String fromFile) throws IOException {
			String expectedLine = firstLineFromFile("src/test/resources/ISO-8859-1.csv",
					"iso-8859-1");
	
			assertThat(fromFile.getBytes("iso-8859-1"))
					.isEqualTo(expectedLine.getBytes("iso-8859-1"));
		}
	
		@Test
		@FileParameters(value = "src/test/resources/x-UTF-16LE-BOM.csv", encoding = "utf-16le")
		public void loadFromUtf16Le(String fromFile) throws IOException {
			String expectedLine = firstLineFromFile(
					"src/test/resources/x-UTF-16LE-BOM.csv", "utf-16le");
	
			assertThat(fromFile.getBytes("utf-16le"))
					.isEqualTo(expectedLine.getBytes("utf-16le"));
		}
	
		private String firstLineFromFile(String filePath, String encoding)
				throws FileNotFoundException {
			Scanner scanner = new Scanner(new InputStreamReader(
					new FileInputStream(filePath), Charset.forName(encoding)));
			return scanner.nextLine();
		}
	*/
	public static class Person {

		private String name;
		private int age;

		public Person(Integer age) {
			this.age = age;
		}

		public Person(String name, Integer age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public boolean isAdult() {
			return age >= 18;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return "Person of age: " + age;
		}
	}

	public static class PersonMapper extends CsvWithHeaderMapper {
		@Override
		public Object[] map(Reader reader) {
			Object[] map = super.map(reader);
			List<Object[]> result = new LinkedList<>();
			for (Object lineObj : map) {
				String line = (String) lineObj;
				result.add(new Object[] { line.substring(2),
						Integer.parseInt(line.substring(0, 1)) });
			}
			return result.toArray();
		}
	}

	public static class JSONMapper implements DataMapper {
		@Override
		public Object[] map(Reader reader) {

			Object[][] resultArray = null;
			List<Object[]> result = new LinkedList<>();
			List<String> lines = new LinkedList<>();
			try {
				BufferedReader br = new BufferedReader(reader);
				String line;
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			JSONObject allTestData = new JSONObject();
			JSONArray rows = new JSONArray();

			try {
				ArrayList<String> hashes = new ArrayList<>();
				String search_keyword = "";
				double expected_count = 0;
				String[] data = new String[lines.size()];
				lines.toArray(data);
				
				// System.err.println("Read: " + String.join("\n", data));
				allTestData = new JSONObject(String.join("\n", data));
				String testName = "test";

				assertTrue(allTestData.has(testName));
				String dataString = allTestData.getString(testName);

				rows = new JSONArray(dataString);
				for (int i = 0; i < rows.length(); i++) {
					String entry = rows.getString(i); // possible
					// org.json.JSONException
					hashes.add(entry);
				}
				assertTrue(hashes.size() > 0);
				for (String entry : hashes) {
					JSONObject entryObj = new JSONObject();
					entryObj = new JSONObject(entry);
					@SuppressWarnings("unchecked")
					Iterator<String> entryKeyIterator = entryObj.keys();

					while (entryKeyIterator.hasNext()) {
						String entryKey = entryKeyIterator.next();
						String entryData = entryObj.get(entryKey).toString();
						switch (entryKey) {
						case "keyword":
							search_keyword = entryData;
							break;
						case "count":
							expected_count = Double.valueOf(entryData);
							break;
						}
					}
					// System.err.println("keyword: " + search_keyword);
					// System.err.println(String.format("count: %.2f", expected_count));
					result.add(new Object[] { search_keyword, expected_count });
				}
				resultArray = new Object[result.size()][];
				result.toArray(resultArray);
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			return resultArray;
		}
	}

	@Ignore
	@Test
	@Parameters(source = ParamSetProvider.class)
	public void paramSetFromClassTest(String str, Integer val) {
		assertThat(str).isEqualTo("a");
		assertThat(val).isEqualTo(1);
	}

	public static class ParamSetProvider {
		public static Object[] provideParamSet() {
			return new Object[] { "a", 1 };
		}
	}

	@Ignore
	@Test
	@CustomParameters(provider = MethodNameReader.class)
	public void getDataMethodName(String name) throws Exception {
		assertThat(name).isEqualTo("getDataMethodName");
	}

	public static class MethodNameReader
			implements ParametersProvider<CustomParameters> {
		private FrameworkMethod frameworkMethod;

		@Override
		public void initialize(CustomParameters parametersAnnotation,
				FrameworkMethod frameworkMethod) {
			this.frameworkMethod = frameworkMethod;
		}

		@Override
		public Object[] getParameters() {
			return new Object[] { frameworkMethod.getName() };
		}
	}

	public static class JSONParametersProvider
			implements ParametersProvider<FileParameters> {

		private FileParameters fileParameters;

		@Override
		public void initialize(FileParameters fileParameters,
				FrameworkMethod frameworkMethod) {
			this.fileParameters = fileParameters;
		}

		@Override
		public Object[] getParameters() {
			return paramsFromFile();
		}

		private Object[] paramsFromFile() {
			try {
				Reader reader = createProperReader();
				DataMapper mapper = fileParameters.mapper().newInstance();
				try {
					return mapper.map(reader);
				} finally {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(
						"Could not successfully read parameters from file: "
								+ fileParameters.value(),
						e);
			}
		}

		private Reader createProperReader() throws IOException {
			String filepath = fileParameters.value();
			String encoding = fileParameters.encoding();

			if (filepath.indexOf(':') < 0) {
				return new InputStreamReader(new FileInputStream(filepath), encoding);
			}

			String protocol = filepath.substring(0, filepath.indexOf(':'));
			String filename = filepath.substring(filepath.indexOf(':') + 1);

			if ("classpath".equals(protocol)) {
				return new InputStreamReader(
						getClass().getClassLoader().getResourceAsStream(filename),
						encoding);
			} else if ("file".equals(protocol)) {
				return new InputStreamReader(new FileInputStream(filename), encoding);
			}

			throw new IllegalArgumentException(
					"Unknown file access protocol. Only 'file' and 'classpath' are supported!");
		}

	}
}
