package co.gem.round;

import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

/**
 * Provide a collection of transactions within an account.
 *
 * @author Julian Del Vergel de Dios (julian@gem.co) on 12/18/14.
 */
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

    /**
     * Requests a payment object to be created by the Gem API.  This will lock UTXOs while you inspect the unsigned
     * payment for things like the suggested fee.
     * @param recipients List of recipients
     * @return Payment - unsigned and not broadcasted
     * @throws java.io.IOException
     * @throws co.gem.round.patchboard.Client.UnexpectedStatusCodeException
     */
    public Transaction create(List<Recipient> recipients, List<Payer> payers,
                              String remainderAccountKey, String changeAccountKey,
                              String network, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException {
        JsonArray payeesJson = new JsonArray();
        for (Recipient recipient : recipients) {
            JsonObject payeeJson = new JsonObject();
            if (recipient.email != null) {
                payeeJson.addProperty("email", recipient.email);
            } else if (recipient.address != null) {
                payeeJson.addProperty("address", recipient.address);
            }

            payeeJson.addProperty("amount", recipient.amount);
            payeesJson.add(payeeJson);
        }

        JsonObject body = new JsonObject();
        body.add("payees", payeesJson);

        if (payers != null) {
            JsonArray payersJson = new JsonArray();
            for (Payer payer : payers) {
                JsonObject payerJson = new JsonObject();
                payerJson.addProperty("amount", payer.amount);
                payerJson.addProperty("account", payer.accountKey);

                payersJson.add(payerJson);
            }
            body.add("payers", payersJson);
        }
        if (remainderAccountKey != null && !remainderAccountKey.equals("")) {
            body.addProperty("remainder_account", remainderAccountKey);
        }
        if (changeAccountKey != null && !changeAccountKey.equals("")) {
            body.addProperty("change_account", changeAccountKey);
        }

        body.addProperty("utxo_confirmations", confirmations);
        if (network != null && !network.equals("")) {
            body.addProperty("network", network);
        }

        Resource paymentResource = resource.action("create", body);

        return new Transaction(paymentResource, this.round);
    }

    public Transaction create(List<Recipient> recipients, int confirmations, String network)
            throws IOException, Client.UnexpectedStatusCodeException {
        JsonArray payeesJson = new JsonArray();
        for (Recipient recipient : recipients) {
            JsonObject payeeJson = new JsonObject();
            if (recipient.email != null) {
                payeeJson.addProperty("email", recipient.email);
            } else if (recipient.address != null) {
                payeeJson.addProperty("address", recipient.address);
            }

            payeeJson.addProperty("amount", recipient.amount);
            payeesJson.add(payeeJson);
        }

        JsonObject body = new JsonObject();
        body.add("payees", payeesJson);
        body.addProperty("utxo_confirmations", confirmations);
        if (network != null && !network.equals("")) {
            body.addProperty("network", network);
        }

        Resource paymentResource = resource.action("create", body);

        return new Transaction(paymentResource, this.round);
    }

    public Transaction create(List<Recipient> recipients, int confirmations)
            throws IOException, Client.UnexpectedStatusCodeException {
        return this.create(recipients, confirmations, null);
    }

    public Transaction create(List<Recipient> recipients)
            throws IOException, Client.UnexpectedStatusCodeException {
        return this.create(recipients, 6);
    }

}
