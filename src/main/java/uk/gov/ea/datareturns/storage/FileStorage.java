package uk.gov.ea.datareturns.storage;

import static uk.gov.ea.datareturns.helper.FileUtilsHelper.makeFullPath;
import static uk.gov.ea.datareturns.helper.FileUtilsHelper.saveFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import uk.gov.ea.datareturns.exception.application.FileKeyMismatchException;
import uk.gov.ea.datareturns.exception.system.FileDeleteException;
import uk.gov.ea.datareturns.exception.system.GeneralServiceException;
import uk.gov.ea.datareturns.helper.FileUtilsHelper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class FileStorage
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileStorage.class);

	public final static String ENV_LOCAL = "local";
	public final static String ENV_DEV = "dev";

	public final static String BUCKET = "data-returns";
	public final static String FOLDER_DEV = "dev";
	public final static String FOLDER_FAILURE = "failure";
	public final static String FOLDER_SUCCESS = "success";
	public final static String SEPARATOR = "/";

	private Jedis fileKeyStorage;
	private String environment;
	private String host;
	private int port;

	public FileStorage(String environment, String host, int port)
	{
		this.environment = environment;
		this.host = host;
		this.port = port;
		this.fileKeyStorage = new Jedis(this.host, this.port);
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setEnvironment(String environment)
	{
		this.environment = environment;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public Jedis getfileKeyStorage()
	{
		return fileKeyStorage;
	}

	public String saveInvalidFile(String fileLocation)
	{
		return storeFile(FOLDER_FAILURE, fileLocation);
	}

	public String saveValidFile(String fileLocation)
	{
		return storeFile(FOLDER_SUCCESS, fileLocation);
	}

	public String retrieveValidFileByKey(String fileKey, String saveFileLocation)
	{
		return retrieveFile(FOLDER_SUCCESS, fileKey, saveFileLocation);
	}

	// TODO needs a transaction
	private String storeFile(String outcome, String fileLocation)
	{
		String fileKey = generateFileKey();

		LOGGER.debug("File key '" + fileKey + "' generated for file '" + fileLocation + "'");

		// Non-local environments use S3
		if (!ENV_LOCAL.equalsIgnoreCase(environment))
		{
			LOGGER.debug("In Non-local environment");

			String fileName = FilenameUtils.getName(fileLocation);
			fileKeyStorage.set(fileKey, fileName);
			LOGGER.debug("File key '" + fileKey + "' saved in Redis with file name '" + fileName + "'");

			String key = makeFileDestinationPath(outcome, fileName);

			try
			{
				AmazonS3 s3client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());

				LOGGER.debug("Saving file '" + fileName + "' to S3 Bucket '" + BUCKET + "' in folder '" + key + "'");
				s3client.putObject(new PutObjectRequest(BUCKET, key, new File(fileLocation)));
				LOGGER.debug("File saved successfully");

				FileUtilsHelper.deleteFile(fileLocation);
			} catch (AmazonServiceException ase)
			{
				throw new GeneralServiceException(ase, "AWS failed to process putObject() request");
			} catch (AmazonClientException ace)
			{
				throw new GeneralServiceException(ace, "General AWS communication failure");
			} catch (IOException e)
			{
				throw new FileDeleteException(e, "Unable to delete file to '" + fileLocation + "'");
			}
		} else
		{
			fileKeyStorage.set(fileKey, fileLocation);
			LOGGER.debug("File key '" + fileKey + "' saved in Redis");
		}

		LOGGER.debug("File stored successfully");

		return fileKey;
	}

	public String retrieveFile(String outcome, String fileKey, String saveFileLocation)
	{
		LOGGER.debug("Retrieving file location from Redis using file key '" + fileKey + "'");

		String fileLocation = fileKeyStorage.get(fileKey);

		if (fileLocation == null)
		{
			throw new FileKeyMismatchException("Unable to locate file using file key '" + fileKey + "'");
		}

		LOGGER.debug("File Location = '" + fileLocation + "'");

		// Non-local environments use S3
		if (!ENV_LOCAL.equalsIgnoreCase(environment))
		{
			LOGGER.debug("In Non-local environment");

			String fileName = FilenameUtils.getName(fileLocation);
			String key = makeFileDestinationPath(outcome, fileName);

			try
			{
				AmazonS3 s3client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());

				LOGGER.debug("Retrieving file '" + fileName + "' from S3 Bucket '" + BUCKET + "'");
				S3Object s3object = s3client.getObject(new GetObjectRequest(BUCKET, key));
				LOGGER.debug("File Retrieved successfully");

				saveFile(s3object.getObjectContent(), makeFullPath(saveFileLocation, fileName));
			} catch (AmazonServiceException ase)
			{
				throw new GeneralServiceException(ase, "AWS failed to process getObject() request");
			} catch (AmazonClientException ace)
			{
				throw new GeneralServiceException(ace, "General AWS communication failure");
			}
		}

		return fileLocation;
	}

	/**
	 * Generate a unique key (uses UUID class for now)
	 * @return
	 */
	public String generateFileKey()
	{
		return UUID.randomUUID().toString();
	}

	private String makeFileDestinationPath(String result, String fileName)
	{
		return environment + SEPARATOR + result + SEPARATOR + fileName;
	}
}
