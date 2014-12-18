package co.gem.round.patchboard

import com.google.gson.JsonObject
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

  def "resources with query"() {
    when:
    def query = new JsonObject();
    query.addProperty("email", "julian@gem.co")
    def resource = client.resources("user_query", query)

    then:
    resource.url.contains("?email=")
  }
}
