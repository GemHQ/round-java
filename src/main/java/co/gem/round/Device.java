package co.gem.round;

import co.gem.round.patchboard.Resource;

/**
 * Created by julian on 4/1/15.
 */
public class Device extends Base {
    public Device(Resource resource, Round round) {
        super(resource, round);
    }

    public String getName() {
        return this.getString("name");
    }

    public String getRedirectURI() {
        return this.getString("redirect_uri");
    }
}
