package sopra.grenoble.jiraLoader.wrappers;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.spring.ApplicationContextProvider;

/**
 * @author cmouilleron
 * Wrapper factory which returns the good wrapper based on the {@link GenericModel#typeDemande} parameter
 */
public class WrapperFactory {
	
	/*
	 * Static logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(WrapperFactory.class);
	
	/**
	 * Return an {@link AbstractWrapper} based on {@link GenericModel#typeDemande} parameter. If a problem occurs, the Null value is returned.
	 * @param row : a row value
	 * @return {@link AbstractWrapper} or null
	 */
	public AbstractWrapper<? extends GenericModel> getWrapper(Row row)  {
		if (row == null) {
			LOG.error("Error while reading line - unable to parse line in model object. row is empty");
			return null;
		}
		
		//get the second column (contain the typeDemande)
		String typeDemande = row.getCell(1).getStringCellValue();

		LOG.info("Reading line <" + row.getRowNum() + "> - type " + typeDemande);
		
		try {
			return (AbstractWrapper<? extends GenericModel>) ApplicationContextProvider.getApplicationContext().getBean("wrapper_" + typeDemande);
		} catch (RuntimeException e) {
			LOG.error("Unable to find th wrapper identified for the type : " + typeDemande);
			return null;
		}
		
	}
}
