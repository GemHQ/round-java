package co.gem.round.patchboard

import spock.lang.Specification

/**
 * Created by julian on 12/3/14.
 */
class ClientSpec extends Specification {
  def patchboard = Patchboard.discover("http://localhost:8999")
  def client = patchboard.spawn(Mock(AuthorizerInterface))

  def "resources"() {
    when:
    def resource = client.resources("users", null)
    then:
    resource.url() == "http://localhost:8999/users"
  }
}
