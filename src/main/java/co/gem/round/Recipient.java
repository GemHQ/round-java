package co.gem.round;

/**
 * Recipients are used as part of the account.pay
 *
 * @author Julian Vergel de Dios (julian@gem.co) on 12/18/14.
 */
public class Recipient {
    public String address;
    public String email;
    public long amount;

    /**
     * Create a recipient object with a bitcoin address and satoshi amount
     * @param address String bitcoin address
     * @param amount Long amount in satoshis
     * @return Recipient
     */
    public static Recipient recipientWithAddress(String address, long amount) {
        Recipient recipient = new Recipient();
        recipient.address = address;
        recipient.amount = amount;
        return recipient;
    }

    @Deprecated
    public static Recipient recipientWithEmail(String email, long amount) {
        Recipient recipient = new Recipient();
        recipient.email = email;
        recipient.amount = amount;
        return recipient;
    }
}
