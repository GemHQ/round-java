package co.gem.round

import spock.lang.Specification

/**
 * Created by julian on 12/19/14.
 */
class RoundSpec extends Specification {

  def round = Round.client(null)

  def "#client returns a valid client"() {
    expect:
      round != null
  }

  def "client returns a valid user_query resource"() {
    when:
      def user = round.user("julian@gem.co")
    then:
      user.url().equals("http://localhost:8999/users?email=julian%40gem.co")

  }

  def "client returns a valid UserCollection resource"() {
    when:
      def users = round.users()
    then:
      users.url().equals("http://localhost:8999/users")
  }

}
