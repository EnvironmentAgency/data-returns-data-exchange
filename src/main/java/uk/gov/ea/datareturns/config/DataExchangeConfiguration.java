package uk.gov.ea.datareturns.config;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataExchangeConfiguration extends Configuration
{
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@Valid
	@NotNull
	private EmailSettings emailsettings = new EmailSettings();

	@Valid
	@NotNull
	private FileStorageSettings fileStorageSettings = new FileStorageSettings();

	@Valid
	@NotNull
	private MiscSettings miscSettings = new MiscSettings();

	@Valid
	@NotNull
	private TestSettings testSettings = new TestSettings();

	@JsonProperty("database")
	public void setDatabase(DataSourceFactory database)
	{
		this.database = database;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory()
	{
		return database;
	}

	@JsonProperty("email")
	public EmailSettings getEmailsettings()
	{
		return emailsettings;
	}

	@JsonProperty("email")
	public void setEmailsettings(EmailSettings emailsettings)
	{
		this.emailsettings = emailsettings;
	}

	@JsonProperty("fileStorage")
	public FileStorageSettings getFileStorageSettings()
	{
		return fileStorageSettings;
	}

	@JsonProperty("fileStorage")
	public void setFileStorageSettings(FileStorageSettings fileStorageSettings)
	{
		this.fileStorageSettings = fileStorageSettings;
	}

	@JsonProperty("misc")
	public MiscSettings getMiscSettings()
	{
		return miscSettings;
	}

	@JsonProperty("misc")
	public void setMiscSettings(MiscSettings miscSettings)
	{
		this.miscSettings = miscSettings;
	}

	@JsonProperty("test")
	public TestSettings getTestSettings()
	{
		return testSettings;
	}

	@JsonProperty("test")
	public void setTestSettings(TestSettings testSettings)
	{
		this.testSettings = testSettings;
	}
}
