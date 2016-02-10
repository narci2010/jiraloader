package sopra.grenoble.jiraLoader.wrappers;

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
	 * @param typeDemande : 
	 * @return {@link AbstractWrapper} or null
	 */
	public AbstractWrapper<? extends GenericModel> getWrapper(String typeDemande)  {
		try {
			return (AbstractWrapper<? extends GenericModel>) ApplicationContextProvider.getApplicationContext().getBean("wrapper_" + typeDemande);
		} catch (RuntimeException e) {
			LOG.error("Unable to find the wrapper identified for the type : " + typeDemande);
			return null;
		}
		
	}
}
