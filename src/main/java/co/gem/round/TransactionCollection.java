package co.gem.round;

import co.gem.round.patchboard.Resource;

public class TransactionCollection extends BaseCollection<Transaction> {

  public TransactionCollection(Resource resource, Round round) {
    super(resource, round);
  }

  @Override
  public void populateCollection(Iterable<Resource> resources) {
    for (Resource resource : resources) {
      Transaction transaction = new Transaction(resource, round);
      add(transaction.key(), transaction);
    }
  }

}
