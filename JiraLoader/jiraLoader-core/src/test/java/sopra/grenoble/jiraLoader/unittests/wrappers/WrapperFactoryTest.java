package sopra.grenoble.jiraLoader.unittests.wrappers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sopra.grenoble.jiraLoader.ApplicationConfiguration;
import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.wrappers.AbstractWrapper;
import sopra.grenoble.jiraLoader.wrappers.EpicWrapper;
import sopra.grenoble.jiraLoader.wrappers.StoryWrapper;
import sopra.grenoble.jiraLoader.wrappers.SubTasksWrapper;
import sopra.grenoble.jiraLoader.wrappers.VersionWrapper;
import sopra.grenoble.jiraLoader.wrappers.WrapperFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class WrapperFactoryTest {
	
	private static WrapperFactory wrapperFact = new WrapperFactory();

	@Test
	public void loadEpicWrapper() {
		AbstractWrapper<?> abstractWrap = (AbstractWrapper<?>) wrapperFact.getWrapper("Epic");
		assertNotNull(abstractWrap);
		assertTrue(abstractWrap instanceof EpicWrapper);
		assertFalse(abstractWrap instanceof StoryWrapper);
	}
	
	@Test
	public void loadVersionWrapper() {
		AbstractWrapper<?> abstractWrap = (AbstractWrapper<?>) wrapperFact.getWrapper("Version");
		assertNotNull(abstractWrap);
		assertTrue(abstractWrap instanceof VersionWrapper);
	}
	
	@Test
	public void loadStoryWrapper() {
		AbstractWrapper<?> abstractWrap = (AbstractWrapper<?>) wrapperFact.getWrapper("Story");
		assertNotNull(abstractWrap);
		assertTrue(abstractWrap instanceof StoryWrapper);
	}
	
	@Test
	public void loadSubTasksWrapper() {
		AbstractWrapper<?> abstractWrap = (AbstractWrapper<?>) wrapperFact.getWrapper("Sous-tâche");
		assertNotNull(abstractWrap);
		assertTrue(abstractWrap instanceof SubTasksWrapper);
	}
}
