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

    /**
     * Looks up a name for the type of blockchain network to use.
     * if it is not found, it defaults to Bitcoin Testnet3
     * @param networkName String
     * @return MulitiWallet.Blockchain
     */
    public static MultiWallet.Blockchain blockchainNetwork(String networkName){
        String name = networkName.toLowerCase();
        if(NETWORK_MAP.containsKey(name)) {
            return NETWORK_MAP.get(name);
        } else {
            return MultiWallet.Blockchain.TESTNET;
        }
    }
}
