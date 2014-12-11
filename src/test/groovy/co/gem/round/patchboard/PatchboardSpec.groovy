package co.gem.round.patchboard

import spock.lang.Specification

class PatchboardSpec extends Specification {
  def patchboard = Patchboard.discover("http://localhost:8999")

  def "discover"() {
    when:
    Patchboard.discover("http://localhost:8999")
    then:
    noExceptionThrown()
  }

  def "spawn"() {
    when:
    patchboard.spawn(Mock(AuthorizerInterface))
    then:
    noExceptionThrown()
  }
}