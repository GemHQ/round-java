package com.bitvault;

import com.bitvault.multiwallet.MultiWallet;

import java.io.IOException;

public interface UnlockedWalletCallback {
	public void execute(MultiWallet wallet) throws IOException, Client.UnexpectedStatusCodeException;
}
