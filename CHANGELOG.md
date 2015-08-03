
0.9.0 / 2015-08-03
==================

  * Added pagination support to Collections (default limit 100 per page)
  * Removed Kalium dependency (and support for nacl-encrypted wallets)

0.7.1 / 2015-04-20
==================

  * Merge pull request #2 from GemHQ/all-the-tests
  * Re-add Utils
  * Add App#reset
  * Only allow one user wallet
  * App auth test!
  * Update tests: enum, device_token
  * Docs: appAuth, transactions, etc
  * No more api_url, app.get instead
  * Delete examples, add getters to Wallet.Wrapper
  * + adminToken, - App_url, instance_id, etc
  *  + Application#userFromKey
  * Query map in subresources, transactions query
  * Docs, Style: camelcase, rename Identity->Identify
  * Add 'Coming soon!'s to the Readmes.
  * DeviceId -> DeviceToken
  * Style: clean up the device auth tests, comments
  * Add defaultAccount() to Wallet
  * Add redirect_uri to Users#create
  * Update java docs (readme, /docs)
  * Gradle.iml
  * Identify Authentication tests
  * Test DeviceAuth actions
  * Style: 2 spaces
  * Generate a backup seed/master on wallet creation
  * Users#create + first/last/device name
  * First / Last name getters for User
  * Add authenticate identify
  * Imports / Dependencies
  * Gitignore
  * wallet wrapper not deprecated for app wallets
  * fixing some example code
  * removing more cruft/fixing a few more mfa details
  * removing more cruft/fixing a few more mfa details
  * added otp auth for application wallets
  * cleaning up functionality for new API
  * removing old unneeded classes
  *  instantiate new MultiWallet objects with seeds instead of keys.
  *  whoops, lost iml
  *  storage the entropy instead of the master node for primary/backup seeds.
  *  upgrade groovy
  * Merge branch 'master' of github.com:GemHQ/round-java
  * Merge branch 'develop'
  * Merge branch 'develop'
  * Merge branch 'feature/1387-add-confirmations-override' into develop
  * added tx/payment cancel
  * fixing signed payment return
  * fixed transaction returning tx objects fixed confirmations not being passed in the json body
  * testing conf override
  * Merge branch 'develop' into feature/1387-add-confirmations-override
  * GP-1387 revert iml write
  * GP-1387 added simple testcase
  * GP-1387 added confirmations override
  * java doc for subscriptions
  * Merge branch 'feature/javadocs' into develop
  * more java doc
  * javadoc for tx and payments
  * javadoc for transactions
  * javadoc for address objects
  * javadoc for accounts
  * added @see to to methods returning collections
  * wallet JavaDoc
  * user/usercollection javaDoc
  * UserCollection JavaDoc
  * Round class javadoc
  * modified license file
  * added subscription classes and associations
  * renamed wallet to default_wallet
  * Merge branch 'feature/GP-1181_add_wallets.create' into develop
  * utils tweaks
  * moving device auth to client instead of user
  * added wallet balance convenience method
  * GP-1181 added wallets.create and functional test in the examples package
  * added primary xpub seed getter
  * Merge branch 'hotfix/GP-1180_payment_npe' into develop
