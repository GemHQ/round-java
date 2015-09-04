package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * AccountCollection is a collection of Gem wallet Accounts.  This class provides the method to create a new account.
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class AccountCollection extends BaseCollection<Account> {
    public static final String RESOURCE_NAME = "accounts";

    private Wallet wallet;

//  public AccountCollection(String url, Round round, Wallet wallet) {
//    super(url, round, RESOURCE_NAME);
//
//    this.wallet = wallet;
//    setWallets();
//  }

    public AccountCollection(Resource resource, Round round, Wallet wallet) {
        super(resource, round);

        this.wallet = wallet;
        setWallets();
    }

    private void setWallets() {
        for (Account account : list) {
            account.setWallet(wallet);
        }
    }

    @Override
    public void populateCollection(Iterable<Resource> resources) {
        for (Resource resource : resources) {
            Account account = new Account(resource, round);
            account.setWallet(this.wallet);
            this.add(account.getString("name"), account);
        }
    }

    /**
     * Creates a new account within a Wallet.
     *
     * @param name
     * @return Account
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.Account
     */
    public Account create(String name, Round.Network network)
            throws IOException, Client.UnexpectedStatusCodeException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("network", network.toString());

        Resource accountResource = resource.action("create", body);

        Account account = new Account(accountResource, this.round);
        account.setWallet(wallet);
        account.fetch();
        this.add(account.key(), account);
        return account;
    }
}
