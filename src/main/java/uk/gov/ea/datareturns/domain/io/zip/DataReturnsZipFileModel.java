package uk.gov.ea.datareturns.domain.io.zip;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import uk.gov.ea.datareturns.domain.model.fields.impl.EaId;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Handles reading and writing returns data to/from zip files
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsZipFileModel {
    /** The directory within the zip fileName where the input fileName is stored (this is the original fileName that was uploaded by the user */
    private static final String DIR_INPUT = "input/";
    /** The directory within the zip fileName used to contain additional metadata */
    private static final String DIR_METADATA = "metadata/";
    /** The directory within the zip fileName where the output files are stored (one output fileName is stored per permit number encountered */
    private static final String DIR_OUTPUT = "output/";
    /** Name of the fileName for output fileName identifier mappings */
    private static final String FILE_OUTPUT_IDENTIFIERS = DIR_METADATA + "outputFileIdentifiers.properties";

    /** The original file data uploaded by the user */
    private byte[] inputData;
    private String inputFileName;

    /** Mapping of output fileName to EA_ID */
    private Map<String, EaId> outputFileIdentifiers;
    /** The output files (one per unique identifier) */
    private Collection<File> outputFiles;

    /**
     * Create a new DataReturnsZipFileModel
     */
    public DataReturnsZipFileModel() {
    }

    public byte[] getInputData() {
        return inputData;
    }

    public void setInputData(byte[] inputData) {
        this.inputData = inputData;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public Map<String, EaId> getOutputFileIdentifiers() {
        return outputFileIdentifiers;
    }

    public void setOutputFileIdentifiers(Map<String, EaId> outputFileIdentifiers) {
        this.outputFileIdentifiers = outputFileIdentifiers;
    }

    /**
     * @return the outputFiles
     */
    public Collection<File> getOutputFiles() {
        return this.outputFiles;
    }

    /**
     * @param outputFiles the outputFiles to set
     */
    public void setOutputFiles(final Collection<File> outputFiles) {
        this.outputFiles = outputFiles;
    }

    /**
     * Add a new output fileName to the list of output files
     *
     * @param outputFile the {@link File} to be added
     */
    public void addOutputFile(final File outputFile) {
        if (this.outputFiles == null) {
            this.outputFiles = new ArrayList<>();
        }
        this.outputFiles.add(outputFile);
    }

    /**
     * Create a ZIP fileName containing the input and output files referenced by this model.
     *
     * @param workFolder the working folder to use on the filesystem
     * @return a reference to the zip fileName that was created
     * @throws IOException if a problem occurred attempting to write the zip fileName.
     */
    public final File toZipFile(final File workFolder) throws IOException {
        final File zipFile = File.createTempFile("drupload", ".zip", workFolder);
        try (
                OutputStream fos = FileUtils.openOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.setLevel(Deflater.BEST_COMPRESSION);

            // Store the input fileName
            ZipEntry entry = new ZipEntry(DIR_INPUT + this.getInputFileName());
            zos.putNextEntry(entry);
            zos.write(this.inputData);
            zos.closeEntry();

            // Store additional metadata
            if (outputFileIdentifiers != null) {
                final Properties outputFileIdProperties = new Properties();
                outputFileIdentifiers.forEach((fileName, eaId) -> {
                    outputFileIdProperties.put(fileName, eaId.getValue().getName());
                });

                entry = new ZipEntry(FILE_OUTPUT_IDENTIFIERS);
                zos.putNextEntry(entry);
                outputFileIdProperties.store(zos, "Mapping from output fileName to EA_ID");
                zos.closeEntry();
            }

            // Store the output files
            for (final File file : this.outputFiles) {
                entry = new ZipEntry(DIR_OUTPUT + file.getName());
                zos.putNextEntry(entry);
                FileUtils.copyFile(file, zos);
                zos.closeEntry();
            }
        }
        return zipFile;
    }

    /**
     * Create a new {@link DataReturnsZipFileModel} from the specified zip fileName
     *
     * @param workFolder the work folder to use for extraction
     * @param zipFileObj the zip fileName to extract the model from
     * @return a new {@link DataReturnsZipFileModel} object
     * @throws IOException if a problem occurred attempting to read the zip fileName
     */
    public static DataReturnsZipFileModel fromZipFile(final File workFolder, final File zipFileObj) throws IOException {
        final DataReturnsZipFileModel zipFileModel = new DataReturnsZipFileModel();

        try (ZipFile zipFile = new ZipFile(zipFileObj)) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                try (InputStream is = zipFile.getInputStream(entry)) {
                    if (FILE_OUTPUT_IDENTIFIERS.equals(entry.getName())) {
                        zipFileModel.outputFileIdentifiers = new HashMap<>();
                        Properties outputFileIdProperties = new Properties();
                        outputFileIdProperties.load(is);

                        outputFileIdProperties.forEach((fileName, eaIdString) -> {
                            zipFileModel.outputFileIdentifiers.put(Objects.toString(fileName), new EaId(Objects.toString(eaIdString)));
                        });
                    } else {
                        if (entry.getName().startsWith(DIR_INPUT)) {
                            zipFileModel.setInputFileName(entry.getName());
                            zipFileModel.setInputData(IOUtils.toByteArray(is));
                        } else {
                            final File tempFile = new File(workFolder, entry.getName());
                            FileUtils.copyInputStreamToFile(is, tempFile);
                            zipFileModel.addOutputFile(tempFile);
                        }
                    }
                }
            }
        }
        return zipFileModel;
    }
}