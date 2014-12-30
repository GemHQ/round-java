package co.gem.round.coinop.util;

import co.gem.round.coinop.MultiWallet;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by jled on 12/29/14.
 */
public class Network {
    private static final Map<String, MultiWallet.Blockchain> NETWORK_MAP = ImmutableMap.<String, MultiWallet.Blockchain>builder()
            .put("mainnet", MultiWallet.Blockchain.MAINNET)
            .put("bitcoin", MultiWallet.Blockchain.MAINNET)
            .put("bitcoin_mainnet", MultiWallet.Blockchain.MAINNET)
            .put("btc", MultiWallet.Blockchain.MAINNET)
            .put("testnet", MultiWallet.Blockchain.TESTNET)
            .put("testnet3", MultiWallet.Blockchain.TESTNET)
            .put("bitcoin_testnet", MultiWallet.Blockchain.TESTNET)
            .put("tbtc", MultiWallet.Blockchain.TESTNET)
            .build();


    public static MultiWallet.Blockchain network(String networkName){
        String name = networkName.toLowerCase();
        if(NETWORK_MAP.containsKey(name)) {
            return NETWORK_MAP.get(name);
        } else {
            throw new Error("network: " + networkName + " not found.");
        }
    }
}
