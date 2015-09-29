package co.gem.round.patchboard.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 11/25/14.
 */
public class ResourceSpec {
    static final String ACTIONS = "actions";

    private Map<String, ActionSpec> actions = new HashMap<String, ActionSpec>();
    private SchemaSpec schemaSpec;

    private ResourceSpec(JsonObject actionsJson) {
        parseActions(actionsJson);
    }

    public static ResourceSpec parse(JsonObject resourceJson) {
        JsonObject actionsJson = resourceJson.get(ACTIONS).getAsJsonObject();

        return new ResourceSpec(actionsJson);
    }

    public void setSchemaSpec(SchemaSpec schemaSpec) { this.schemaSpec = schemaSpec; }
    public SchemaSpec schemaSpec() { return schemaSpec; }
    public ActionSpec action(String name) { return actions.get(name); }

    private void parseActions(JsonObject actionsJson) {
        for(Map.Entry<String, JsonElement> entry : actionsJson.entrySet()) {
            ActionSpec actionSpec = ActionSpec.parse(entry.getValue().getAsJsonObject());
            actions.put(entry.getKey(), actionSpec);
        }
    }
}
