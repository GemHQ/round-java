# round-java: Advanced Topics

## Wallets and Accounts

### Wallets
The wallet itself is a BIP32 hierarchical deterministic (HD) wallet.  The Gem wallet takes the approach of calling the root node a wallet.  Going to depth1 gets you to the Account nodes and depth2 the addresses underneath the accounts.  

The Gem wallet has convenience methods to make managing the wallet easy to do.  There are key methods to use off of the wallet object:

* `wallet.balance();`: returns the total balance of all accounts
* `wallet.accounts();`: returns a collection of round accounts.

[[top]](README.md#round-rb-advanced-topics) [[back]](../README.md)

### Accounts
A gem account is the main object to interact with.  The account is where payments are made from and where you access transaction collections.  The gem wallet can have many accounts.  As mentioned in the wallet section, a Gem account within a wallet is a collection of bitcoin addresses and the complexity of dealing with addresses is now abstracted away.  

The key methods on an account to use are:

* `account.balance();`: returns the sum of all transactions with 1 or more confirmations
* `account.pendingBalance();`: returns the sum of all incoming outgoing transactions with 0 confirmations
* `account.pay(payees,amount,confirmations);`: send bitcoin out of an account **must call [wallet.unlock()](advanced.md#wallets) first**
* `account.transactions();`: return the collections of transactions

A pending_balance in Gem is any address involved in a transaction with 0 confirmations.  This means that in multiple transactions both incoming and outgoing will produce a net pending_balance.  As they confirm with a single confirmation, the account balance in the API reflects the change.  Objects get cached for speed in the client, so to fetch a new state of an account on the API, call account = account.refresh().

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

## Transactions and Payments
Transaction collections have a relationship to an account. You can specify the following query parameters for transactions:
* `status: ['unsigned', 'unconfirmed', 'confirmed', 'rejected', 'canceled']`
* `type: ['incoming', 'outgoing', 'transfer_in', 'transfer_out', 'change']`

`txs = account.transactions(type: 'incoming')`

 Now lets look at a single transaction: `tx = txs[0]`

There is a lot of information on the tx.  You can call the attributes to get at the full list `tx.attributes`.  Additionally there are some convenience methods to get at key information quickly.  For example, `tx.hash` returns the transaction hash.

### Fee Estimation
Fees are estimated by requesting for an unsigned transaction from the API.  The Gem API will then lock the unspent outputs to prevent a potential double spend.  The returned unsigned transaction will have a fee in the attributes that you can then inspect.  If you decide you don't want to perform the transaction you'll have to [cancel the transaction](advanced.md#canceling-unsigned-transaction) [[back]](../README.md)

Example snippet to generate an unsigned transaction:

```java
account = wallet.defaultAccount();
String toAddress = '2N4MtK1rZ88UWXDGWWVf1gYz1Runj4FMDr7'
Recipient recipient = Recipient.recipientWithAddress(toAddress, 12000);
List<Recipient> recipients = Collections.singletonList(recipient);
Transaction unsigned = account.transactions().create(recipients, 6);
long fee = unsigned.getFee();
```

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Canceling Unsigned Transaction
You can accomplish this by calling `tx.cancel()` on a transaction.  If you have a lot of transactions you can loop over the collection and cancel.

```java
// Type 'Transaction'
transaction.cancel();
```

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

## Attributes and Refresh
### Attributes
All objects in the round client have attributes in a key/value store.  If you want to see information within the attributes all you have to do is access it like any k/v object.

to see all the attributes of an object:

```java
System.out.println(account.resource().attributes());
```
To access a particular attribute:

```java
account.getString('something');
```

__If there are no convenience methods for attributes you use often, please file an issue with what you need or make a pr if you build it in yourself.__

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Refresh()
The data on objects are cached client-side for performance versuses having to make API calls for every single method.  What this also means is that if you have for example an instance method for an account, then the information on the account could get into a stale state.  You will have to trigger a refresh of the object with any changes from the API. 

When calling fetch, the object will be returned with the updated information.  
Fetch can be called on individual objects as well as the corresponding collections.  For example:

* `account.fetch();`: returns void and updates the record
* `accountCollection.fetch()`: updates collection

[[top]](README.md#round-rb-advanced-topics) [[back]](../README.md)

## Subscriptions
Setting up a subscription on your application will allow you to be notified via a webhook about any incoming/outgoing transaction for any address associated with an account in a wallet of your users.  There is no need to manage webhooks at an address level anymore.  Gem's API will automatically register any new address or change address added to accounts automatically.  When a subscription is triggered, Gem will attempt delivery to the provided callback_url.  If your app server does not respond with a 200, Gem will continue to try.

### Configure the Application

1.  Go to the console and add a `subscription token` to the application.  This token is shared with the API and Gem will embed the token in any subscription notification that is sent to your app.

1. Expand the application by clicking on the name.  You will see a section called “subscriptions”

1. Click the “add new subscription”  and provide the callback_url .  Any new address added to any users wallet authorized on your app will automatically registered for you.

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Webhook operations
You will start to receive a webhook subscription at the provided url for incoming/outgoing transactions.  The payload of the subscription will contain information about the transaction, amount, and UIDs for the user/wallet/account information.  You’ll be able to use this information to query your app.

For example - the following snippet will retrieve the user in a given subscription 

```java
// Coming soon!
```

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

##  Integrated 2FA
Gem has built 2FA into the API but additionally built a system to add additional 2FA challenges to your app, so you don’t have to integrate yet another api.  You can ask Gem to send an sms challenge to the user to then pass back to your app.  The user will not get an SMS if the user has a TOTP app installed like Google Authenticator, Authy, Duo etc.  

Example of how to incorporate 2FA into your app.

```java
// Coming soon!
```

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

## Operational/Custodial Wallets
There are certain scenarios where you want to implement a wallet that you are in posession of that is used for business or custodial purposes.  In the operational/custodial model you will have two keys, the primary used for daily signing and the backup used for recovery.  This means that you hold funds be it the business or your end users.

### Configure 

* Create a new instance token in the management console.  
	* Instance tokens are used in the application authentication scheme.  When authenticating as an application, you will have full control of the applications wallets and allows a read only view of end user data if your app supports both.
* __Keep the token safe__

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Authenticate
To authenticate as an application to get to an application wallet and/or pull information about the application call:

```java
// Coming soon!
```

[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Wallet creation (Soon)
[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)

### Payments (Soon)
[[top]](README.md#round-java-advanced-topics) [[back]](../README.md)
