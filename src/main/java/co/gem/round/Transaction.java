package co.gem.round;

import co.gem.round.coinop.MultiWallet;
import co.gem.round.coinop.TransactionWrapper;
import co.gem.round.patchboard.Client;
import co.gem.round.patchboard.Resource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Transaction associated with an account.  Contains hash, type, status and the bitcoin transaction json.
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Transaction extends Base {
    public enum Status {
        UNCONFIRMED("unconfirmed"),
        CONFIRMED("confirmed"),
        REJECTED("rejected"),
        CANCELED("canceled"),
        UNSIGNED("unsigned");
        private String status;
        Status (String status) {
            this.status = status;
        }
        public String toString() {
            return this.status;
        }
    }
    public enum Type {
        INCOMING("incoming"),
        OUTGOING("outgoing"),
        TRANSFER_IN("transfer_in"),
        TRANSFER_OUT("transfer_out"),
        CHANGE("change");
        private String type;
        Type (String type) {
            this.type = type;
        }
        public String toString() {
            return this.type;
        }
    }

    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    public Transaction(Resource resource, Round round) {
        super(resource, round);
    }

    /**
     * Sign a Gem API unsigned payment to send back to Gem for rules verification, co-signing and publishing to the
     * blockchain
     * @param wallet coinop.MultiWallet
     * @return Payment signed
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     * @see co.gem.round.coinop.MultiWallet
     */
    public Transaction sign(MultiWallet wallet)
            throws IOException, Client.UnexpectedStatusCodeException {
        List<String> signatures = wallet.signaturesFromUnparsedTransaction(resource.attributes());

        JsonArray signaturesJson = new JsonArray();
        for (String signature : signatures) {
            JsonObject signatureJson = new JsonObject();
            signatureJson.addProperty("primary", signature);
            signaturesJson.add(signatureJson);
        }

        JsonObject transactionSignatures = new JsonObject();
        transactionSignatures.addProperty("transaction_hash", resource.attributes().get("hash").getAsString());
        transactionSignatures.add("inputs", signaturesJson);
        JsonObject body = new JsonObject();
        body.add("signatures", transactionSignatures);

        Resource signedPayment = resource.action("update", body);
        resource = signedPayment;
        return this;
    }

    public String getMfaUri() {
        JsonElement possible = getAttribute("mfa_uri");
        if (possible != null && !possible.isJsonNull()) {
            return possible.getAsString();
        }
        return null;
    }

    public void setRedirectUri(String redirectUri) {
        resource.attributes().addProperty("redirect_uri", redirectUri);
    }

    public Transaction approve()
            throws IOException, Client.UnexpectedStatusCodeException {
        resource = this.resource().action("approve", new JsonObject());
        return this;
    }

    /**
     * Cancels an unsigned transaction
     * @throws IOException
     * @throws Client.UnexpectedStatusCodeException
     */
    public void cancel()
            throws IOException, Client.UnexpectedStatusCodeException {
        this.resource.action("cancel");
    }

    /**
     * Getter for the transaction type.  "incoming" and "outgoing" are the two types of values to be returned
     * @return String type either "incoming" or "outgoing"
     */
    public String getType() {
        return this.resource().attributes().get("type").getAsString();
    }

    /**
     * Getter for the bitcoin transaction hash
     * @return String bitcoin transaction hash
     */
    public String getTransactionHash() {
        return this.resource().attributes().get("hash").getAsString();
    }

    /**
     * Getter for the value in the bitcoin transaction.
     * @return Long value
     */
    public long getValue() {
        return this.resource().attributes().get("value").getAsLong();
    }

    /**
     * Getter for the status of a bitcoin transaction.  Will return confirmed, unconfirmed, canceled, unsigned
     * @return String status: confirmed, unconfirmed, canceled, unsigned
     */
    public String getStatus() {
        return this.resource().attributes().get("status").getAsString();
    }

    /**
     * Getter for the number of confirmations of a transaction
     * @return int number of confirmations
     */
    public int getConfirmations() {
        return this.resource().attributes().get("confirmations").getAsInt();
    }

    /**
     * Getter for the fee in the transaction.  Value - Fee = amount sent
     * @return
     */
    public long getFee() {
        return this.resource().attributes().get("fee").getAsLong();
    }

    /**
     * Getter for the date created for a bitcoin transaction yyyy-MM-dd HH:mm:ss Z
     * @return Date created at
     */
    public Date getCreatedAt() {
        String dateString = this.resource().attributes().get("created_at").getAsString();

        Date createdAt = null;
        try {
            createdAt = DATE_FORMATTER.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createdAt;
    }
}
