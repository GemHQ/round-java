package co.gem.round.patchboard

import com.google.gson.JsonObject
import spock.lang.Specification

/**
 * Created by julian on 12/15/14.
 */

def

class ResourceSpec extends Specification {
  def patchboard = Patchboard.discover("http://localhost:8999")
  def client = patchboard.spawn(Mock(AuthorizerInterface))

  def "action with incorrect body"() {
    def resource = client.resources("users")
    when:
    resource.action("create")
    then:
    thrown(Client.UnexpectedStatusCodeException)
  }
  
  def "action with correct body"() {
    setup:
    def pubkey = """
    -----BEGIN PUBLIC KEY-----
    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BbS/GI9R3XnLphBoYGS
    1TWWORxD1InKSwiIE3ZFx7WXGz52C8Xzo+Zagy2Pb86C6iKq2k66Xkj1UGBJD2P6
    9Tpg5TBEoArb+kD5hkkw+Ta39n/TM/VqbfAiV1iAR6i2+TGTrCWiT1IRHvTG/do/
    XD+ESRYH9W/ppAoLCpniS5vOx+Bb3nYq7RCo+ESVjDPSjgcASqXVdVBues8O2iua
    Stc8oepJBNReZ5eWKkOl3ST2C9SiUsmzLrnBDVDlq0fB13ruhG7eWevP09pNMRBZ
    d8HB9AlZiaPA82PNPKj38xOBUKi51gIYoEphCPtbicWe2T/tFvpNNXHQi98nE43o
    OQIDAQAB
    -----END PUBLIC KEY-----
    """
    def developer_body = new JsonObject()
    developer_body.addProperty("email", "user-" + System.currentTimeMillis() + "@gem.co")
    developer_body.addProperty("pubkey", pubkey)
    def resource = client.resources("developers")

    when:
    def new_resource = resource.action("create", developer_body)

    then:
    noExceptionThrown()
    new_resource.url().contains("developers")
  }
}
