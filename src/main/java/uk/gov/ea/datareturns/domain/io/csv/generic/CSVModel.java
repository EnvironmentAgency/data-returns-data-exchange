package uk.gov.ea.datareturns.domain.io.csv.generic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CSVModel<T extends Object> {
	/**
	 * Provides a mapping between the Java Object field and the header name to which it is mapped
	 */
	private Map<String, String> pojoFieldToHeaderMap = new LinkedHashMap<>();

	/**
	 * A list of records in document order
	 */
	private List<T> records;

	/**
	 * Default constructor
	 */
	public CSVModel() {

	}

	/**
	 * @return the pojoFieldToHeaderMap
	 */
	public Map<String, String> getPojoFieldToHeaderMap() {
		return this.pojoFieldToHeaderMap;
	}

	/**
	 * @param pojoFieldToHeaderMap the pojoFieldToHeaderMap to set
	 */
	public void setPojoFieldToHeaderMap(final Map<String, String> pojoFieldToHeaderMap) {
		this.pojoFieldToHeaderMap = pojoFieldToHeaderMap;
	}

	/**
	 * @return the records
	 */
	public List<T> getRecords() {
		return this.records;
	}

	/**
	 * @param records the records to set
	 */
	public void setRecords(final List<T> records) {
		this.records = records;
	}
}
