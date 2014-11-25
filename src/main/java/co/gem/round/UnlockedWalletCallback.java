package co.gem.round;

import co.gem.round.coinop.MultiWallet;

import java.io.IOException;

public interface UnlockedWalletCallback {
  public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException;
}
