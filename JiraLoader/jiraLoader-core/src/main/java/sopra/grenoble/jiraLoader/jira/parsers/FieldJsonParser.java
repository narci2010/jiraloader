package sopra.grenoble.jiraLoader.jira.parsers;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;

/**
 * @author cmouilleron
 *
 */
public class FieldJsonParser implements JsonObjectParser<Field> {

	@Override
	public Field parse(JSONObject json) throws JSONException {
		final String name = json.getString("name");
		final String id = json.getString("id");
		return new Field(id, name, null, null);
	}

}
