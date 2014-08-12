package com.bitvault;

import com.bitvault.multiwallet.MultiWallet;

public interface UnlockedWalletCallback {
	public void execute(MultiWallet wallet);
}
