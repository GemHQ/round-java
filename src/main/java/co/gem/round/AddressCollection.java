package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;

import java.io.IOException;

/**
 * Collection class for addresses.  AddressCollection provides access to the create method to generate a new address
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class AddressCollection extends BaseCollection<Address> {
    public AddressCollection(Resource resource, Round round) { super(resource, round); }

    /**
     *  Generates a new address within an account.
     * @return Address
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.Address
     */
    public Address create()
            throws IOException, Client.UnexpectedStatusCodeException {
        Resource addressResource = resource.action("create");
        return new Address(addressResource, this.round);
    }

    @Override
    public void populateCollection(Iterable<Resource> resources) {
        for (Resource resource : resources) {
            Address address = new Address(resource, round);
            this.add(address.getString("string"), address);
        }
    }
}
