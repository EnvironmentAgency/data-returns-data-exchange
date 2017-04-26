package uk.gov.ea.datareturns.tests.integration.resource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ea.datareturns.App;
import uk.gov.ea.datareturns.config.TestSettings;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Dataset;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.Record;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.User;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.processors.SubmissionProcessor;
import uk.gov.ea.datareturns.domain.result.ValidationErrors;
import uk.gov.ea.datareturns.tests.integration.model.SubmissionProcessorTests;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * @author Graham Willis
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SubmissionIntegrationTests {
    @Inject SubmissionService submissionService;
    @Inject SubmissionProcessor<DataSample> submissionProcessor;
    @Inject private TestSettings testSettings;

    private final static String SUCCESSFUL_SUBMISSION = "json/success-multiple.json";

    private final String USER_NAME = "Graham Willis";
    private final String DATASET_ID = "SEP2018Q2";
    private User user;
    private Dataset dataset;

    // Remove any old data and set a user and dataset for use in the tests
    @Before
    public void init() {
        if (submissionService.getUser(USER_NAME) != null) {
            submissionService.removeUser(USER_NAME);
        }
        user = submissionService.createUser(USER_NAME);
        dataset = submissionService.createDataset(user);
    }

    @Test
    public void validationAndSubmission() throws IOException {
        List<DataSample> samples = submissionProcessor.parse(readTestFile(SUCCESSFUL_SUBMISSION));
        ValidationErrors validationErrors = submissionProcessor.validate(samples);
        Assert.assertTrue(validationErrors.isValid());
        long count = submissionService.submit(dataset, samples);
        Assert.assertEquals(4, count);
    }

    @Test
    public void validationAndSubmissionAndRemoval() throws IOException {
        List<DataSample> samples = submissionProcessor.parse(readTestFile(SUCCESSFUL_SUBMISSION));
        ValidationErrors validationErrors = submissionProcessor.validate(samples);
        Assert.assertTrue(validationErrors.isValid());
        long count = submissionService.submit(dataset, samples);
        Assert.assertEquals(4, count);
        List<Dataset> datasets = submissionService.getDatasets(user);
        List<Record> records = submissionService.getRecords(datasets.get(0));
        Assert.assertEquals(4, records.size());
        submissionService.removeDataset(datasets.get(0).getIdentifier());
        datasets = submissionService.getDatasets(user);
        Assert.assertEquals(0, datasets.size());
    }

    private String readTestFile(String testFileName) throws IOException {
        final String testFilesLocation = this.testSettings.getTestFilesLocation();
        final File testFile = new File(testFilesLocation, testFileName);
        InputStream inputStream = SubmissionProcessorTests.class.getResourceAsStream(testFile.getAbsolutePath());
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }
}
