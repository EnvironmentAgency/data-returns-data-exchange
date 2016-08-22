package uk.gov.ea.datareturns.tests.resource;

import com.univocity.parsers.common.TextParsingException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.io.csv.CSVColumnReader;
import uk.gov.ea.datareturns.domain.io.zip.DataReturnsZipFileModel;
import uk.gov.ea.datareturns.domain.jpa.dao.QualifierDao;
import uk.gov.ea.datareturns.domain.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Qualifier;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;
import uk.gov.ea.datareturns.domain.model.rules.DataReturnsHeaders;
import uk.gov.ea.datareturns.domain.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.domain.storage.StorageException;
import uk.gov.ea.datareturns.domain.storage.StorageProvider;
import uk.gov.ea.datareturns.domain.storage.StorageProvider.StoredFile;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Processor Integration Tests
 *
 * The purpose of tests in this class is to check that for a given input file the output is transformed as expected.
 *
 * For example, we accept boolean values true, false, yes, no, 1, 0 (or any case variation of this) but should always output
 * boolean values using the standard true/false notation
 *
 * @author Sam Gardner-Dell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("IntegrationTests")
@DirtiesContext
public class ProcessorIntegrationTests {
	public final static String IO_TESTS_FOLDER = "/testfiles/iotests/";
	public final static String BOOLEAN_TESTS = "boolean-values.csv";

	public final static String RTN_TYPE_SUB = "testReturnTypeSubstitution.csv";
	public final static String QUALIFIER_SUB = "testQualifierSubstitution.csv";

	@Inject
	private ApplicationContext context;

	@Inject
	private StorageProvider storage;

	@Inject
	private ReturnTypeDao returnTypeDao;

	@Inject
	private QualifierDao qualifierDao;

	/**
	 * Tests boolean values are converted as necessary.
	 */
	@Test
	public void testBooleanValues() {
		final Collection<File> outputFiles = getOutputFiles(getTestFileStream(BOOLEAN_TESTS));
		Assertions.assertThat(outputFiles.size()).isEqualTo(1);
		final String[] expected = { "true", "false", "true", "false", "true", "false", "true", "false" };
		verifyCSVValues(outputFiles.iterator().next(), DataReturnsHeaders.TEXT_VALUE, expected);
	}

	@Test
	public void ReturnTypeValues() {
		final Collection<File> outputFiles = getOutputFiles(getTestFileStream(RTN_TYPE_SUB));
		Assertions.assertThat(outputFiles.size()).isEqualTo(1);
		final List<ReturnType> returnTypes = returnTypeDao.list();
		final List<String> returnTypeNames = returnTypes.stream().map(ReturnType::getName).collect(Collectors.toList());
		verifyExpectedValuesContainsCSVValues(outputFiles.iterator().next(), DataReturnsHeaders.RETURN_TYPE, returnTypeNames);
	}

	@Test
	public void QualifierValuesValues() {
		final Collection<File> outputFiles = getOutputFiles(getTestFileStream(QUALIFIER_SUB));
		Assertions.assertThat(outputFiles.size()).isEqualTo(1);
		final List<Qualifier> qualifiers = qualifierDao.list();
		final List<String> qualifierNames = qualifiers.stream().map(Qualifier::getName).collect(Collectors.toList());
		verifyExpectedValuesContainsCSVValues(outputFiles.iterator().next(), DataReturnsHeaders.QUALIFIER, qualifierNames);
	}

	/**
	 * Retrieve the set of output files which are created by the processors for the given {@link InputStream}
	 *
	 * @param inputStream the {@link InputStream} containing DEP compliant
	 * @return
	 */
	private Collection<File> getOutputFiles(final InputStream inputStream) {
		final FileUploadProcessor processor = this.context.getBean(FileUploadProcessor.class);

		processor.setClientFilename("ProcessorIntegrationTests.csv");
		processor.setInputStream(inputStream);

		DataExchangeResult result = null;
		try {
			result = processor.process();
		} catch (final ProcessingException e) {
			Assertions.fail("Processor exception thrown", e);
		}
		Assertions.assertThat(result.getParseResult().getMappings().isEmpty()).isFalse();
		Assertions.assertThat(result.getUploadResult().getFileKey()).isNotEmpty();

		final String fileKey = result.getUploadResult().getFileKey();
		try {
			final StoredFile storedFile = this.storage.retrieveTemporaryData(fileKey);
			final File workingFolder = org.assertj.core.util.Files.temporaryFolder();
			final DataReturnsZipFileModel zipModel = DataReturnsZipFileModel.fromZipFile(workingFolder, storedFile.getFile());
			return zipModel.getOutputFiles();
		} catch (StorageException | IOException e) {
			throw new AssertionError("Unable to retrieve stored file.", e);
		}
	}

	/**
	 * For a given output CSV file, check that the values in the specified column match those that are expected
	 *
	 * @param csvFile the CSV file to parse
	 * @param columnName the column header of the data to be checked
	 * @param expectedValues the expected values to be found in the column (in document order)
	 */
	private static void verifyCSVValues(final File csvFile, final String columnName, final String[] expectedValues) {
		try {
			final List<String> columnData = CSVColumnReader.readColumn(csvFile, columnName);
			Assertions.assertThat(expectedValues.length).isEqualTo(columnData.size());

			for (int i = 0; i < columnData.size(); i++) {
				Assertions.assertThat(columnData.get(i))
						.as("Mismatched value on row " + (i + 1))
						.isEqualTo(expectedValues[i]);
			}
		} catch (final TextParsingException e) {
			throw new AssertionError("Unable to parse output CSV file.", e);
		}
	}

	/**
	 * For a given output CSV file, check that the values in the specified column are contained by the list expected
	 *
	 * @param csvFile the CSV file to parse
	 * @param columnName the column header of the data to be checked
	 * @param expectedValues the expected values to be found in the column
	 */
	private static void verifyExpectedValuesContainsCSVValues(final File csvFile, final String columnName, final List<String> expectedValues) {
		try {
			final List<String> columnData = CSVColumnReader.readColumn(csvFile, columnName);
			for (int i = 0; i < columnData.size(); i++) {
                if (columnData.get(i) != null) {
                    Assertions.assertThat(expectedValues.contains(columnData.get(i)))
                            .as("Not found: " + columnData.get(i))
                            .isTrue();
                }
			}
		} catch (final TextParsingException e) {
			throw new AssertionError("Unable to parse output CSV file.", e);
		}
	}

	/**
	 * Return an InputStream for a given test file
	 *
	 * @param testFile the name of the test file to resolve
	 * @return an {@link InputStream} from the test file
	 */
	private static InputStream getTestFileStream(final String testFile) {
		return ProcessorIntegrationTests.class.getResourceAsStream(IO_TESTS_FOLDER + testFile);
	}
}