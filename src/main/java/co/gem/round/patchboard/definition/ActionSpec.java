package co.gem.round.patchboard.definition;

import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

/**
 * Created by julian on 11/25/14.
 */
public class ActionSpec {
    static final String METHOD = "method";
    static final String REQUEST = "request";
    static final String RESPONSE = "response";

    private String method;
    private RequestSpec request;
    private ResponseSpec response;

    private ActionSpec(String method, RequestSpec request, ResponseSpec response) {
        this.method = method;
        this.request = request;
        this.response = response;
    }

    public static ActionSpec parse(JsonObject actionJson) {
        String method = actionJson.get(METHOD).getAsString();
        RequestSpec request = null;
        if (actionJson.has(REQUEST))
            request = RequestSpec.parse(actionJson.get(REQUEST).getAsJsonObject());
        ResponseSpec response = ResponseSpec.parse(actionJson.get(RESPONSE).getAsJsonObject());
        return new ActionSpec(method, request, response);
    }

    public String method() {
        return method;
    }

    public RequestSpec request() {
        return request;
    }

    public ResponseSpec response() {
        return response;
    }
}
