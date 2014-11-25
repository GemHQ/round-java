package co.gem.round;

public class Recipient {
  public String address;
  public String email;
  public long amount;

  public static Recipient recipientWithAddress(String address, long amount) {
    Recipient recipient = new Recipient();
    recipient.address = address;
    recipient.amount = amount;
    return recipient;
  }


  public static Recipient recipientWithEmail(String email, long amount) {
    Recipient recipient = new Recipient();
    recipient.email = email;
    recipient.amount = amount;
    return recipient;
  }
}
