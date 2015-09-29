package co.gem.round;

import co.gem.round.patchboard.Resource;

/**
 * Address objects provide getters to the address string and BIP44 path within a Gem Wallet.
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Address extends Base {

    public Address(Resource resource, Round round) {
        super(resource, round);
    }

    /**
     * Getter for the Address string from the Address object.
     * @return String address
     */
    public String getAddressString() {
        return getString("string");
    }

    /**
     * Getter for the path within the wallet.
     * @return String wallet path to the address
     */
    public String getAddressPath() {
        return getString("path");
    }
}


