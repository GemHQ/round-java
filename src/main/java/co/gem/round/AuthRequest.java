package co.gem.round;

public class AuthRequest {
  private String mfaUri;
  private String deviceToken;

  public AuthRequest(String mfaUri, String deviceToken) {
    this.mfaUri = mfaUri;
    this.deviceToken = deviceToken;
  }

  public String getMfaUri() {
    return mfaUri;
  }

  public String getDeviceToken() {
    return deviceToken;
  }
}
