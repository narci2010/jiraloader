package sopra.grenoble.jiraLoader.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author cmouilleron
 *	Class which allows to get the Spring application context
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
	
	/**
	 * The spring application context
	 */
	private static ApplicationContext ctx = null;

	/**
	 * Return the Sring application context
	 * @return {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext ctxp) {
		ctx = ctxp;
	}
}