package co.gem.round.util

import spock.lang.Specification

/**
 * Created by julian on 12/30/14.
 */
class HttpSpec extends Specification {
  def test_string = "Gem-OOB-OTP realm=\"user.authorize_device\", key=\"otp.8iDwRaoR9ohlqbfCaQX19w\""

  def "it correctly parses a header"() {
    when:
    def Map<String, String> params = Http.extractParamsFromHeader(test_string)

    then:
    params.get("key") == "otp.8iDwRaoR9ohlqbfCaQX19w"
    params.get("realm") == "user.authorize_device"
  }
}
