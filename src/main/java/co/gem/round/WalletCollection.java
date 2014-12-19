package co.gem.round;

import co.gem.round.patchboard.Resource;

/**
 * Created by julian on 12/18/14.
 */
public class WalletCollection extends BaseCollection<Wallet> {
  public WalletCollection(Resource resource, Round round) {
    super(resource, round);
  }

  @Override
  public void populateCollection(Iterable<Resource> collection) {
    for (Resource resource : collection) {
      Wallet wallet = new Wallet(resource, round);
      add(wallet.getString("name"), wallet);
    }
  }
}
