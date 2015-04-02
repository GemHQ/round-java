package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by julian on 4/1/15.
 */
public class DeviceCollection extends BaseCollection<Device> {
  public DeviceCollection(Resource resource, Round round) {
    super(resource, round);
  }

  @Override
  public void populateCollection(Iterable<Resource> collection) { }

  public Device create(String name)
      throws IOException, Client.UnexpectedStatusCodeException {
    JsonObject body = new JsonObject();
    body.addProperty("name", name);

    Resource deviceResource = resource.action("create", body);

    Device device = new Device(deviceResource, this.round);
    this.add(device.getName(), device);
    return device;
  }
}
