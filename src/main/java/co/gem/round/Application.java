package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by julian on 12/18/14.
 *
 */
public class Application extends Base{
    public Application(Resource resource, Round round) {
        super(resource, round);
    }

    /**
     * Getter for users collection. Returns populated UserCollection object. To
     * retrieve reference without fetching users use 'users(false)'
     * @return UserCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.UserCollection
     */
    public UserCollection users()
            throws IOException, Client.UnexpectedStatusCodeException {
        return users(true);
    }

    /**
     * Getter for users collection.
     * @param fetch boolean used to determine whether to populate collection
     * @return UserCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.UserCollection
     */
    public UserCollection users(boolean fetch)
            throws IOException, Client.UnexpectedStatusCodeException {
        Resource usersResource = resource.subresource("users");
        UserCollection users = new UserCollection(usersResource, round);
        if (fetch) {
            users.fetch();
        }
        return users;
    }

    public User userFromKey(String key) throws IOException, Client.UnexpectedStatusCodeException {
        for(User user : users()) {
            if (user.key().equals(key)) {
                return user;
            }
        }
        return null;
    }

    public void setTotpSecret(String totpSecret) {
        round.patchboardClient().authorizer().setOtpSecret(totpSecret);
    }

    public Application reset(String ...tokens) throws IOException, Client.UnexpectedStatusCodeException {
        JsonArray body = new JsonArray();
        for (String token : tokens) {
            body.add(new JsonPrimitive(token));
        }
        resource = resource.action("reset", body);
        return this;
    }

    /**
     * Getter for wallets. Returns populated WalletCollection object. To
     * retrieve reference without fetching wallets use 'wallets(false)'
     * @return WalletCollection of wallets
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.WalletCollection
     */
    public WalletCollection wallets()
            throws IOException, Client.UnexpectedStatusCodeException {
        return wallets(true);
    }

    /**
     * Getter for WalletCollection object.
     * @param fetch boolean used to determine whether to populate collection
     * @return WalletCollection
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.WalletCollection
     */
    public WalletCollection wallets(boolean fetch)
            throws IOException, Client.UnexpectedStatusCodeException {
        Resource walletsResource = resource.subresource("wallets");
        WalletCollection wallets = new WalletCollection(walletsResource, round, this);
        if (fetch) {
            wallets.fetch();
        }
        return wallets;
    }

    /**
     * Gets a wallet with name matching walletName parameter.
     * @param walletName Wallet name
     * @return Wallet
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     */
    public Wallet wallet(String walletName)
            throws IOException, Client.UnexpectedStatusCodeException {
        Map<String, String> query = new HashMap<>();
        query.put("name", walletName);
        Wallet wallet = new Wallet(resource.subresource("wallet_query", query), round, this);
        wallet.fetch();
        return wallet;
    }
}
