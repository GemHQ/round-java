package co.gem.round;

import co.gem.round.multiwallet.MultiWallet;

import java.io.IOException;

public interface UnlockedWalletCallback {
	public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException;
}
