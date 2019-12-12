package com.EMS.security;

import java.util.Base64;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdToken {
  @JsonProperty("exp")
  private long expirationTime;
  @JsonProperty("nbf")
  private long notBefore;
  @JsonProperty("tid")
  private String tenantId;
  private String nonce;
  private String name;
  private String email;
  @JsonProperty("preferred_username")
  private String preferredUsername;
  @JsonProperty("oid")
  private String objectId;

  public static IdToken parseEncodedToken(String encodedToken, String nonce) {

    String[] tokenParts = encodedToken.split("\\.");

    String idToken = tokenParts[1];

    byte[] decodedBytes = Base64.getUrlDecoder().decode(idToken);

    ObjectMapper mapper = new ObjectMapper();
    IdToken newToken = null;
    try {
      newToken = mapper.readValue(decodedBytes, IdToken.class);
      if (!newToken.isValid(nonce)) {
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return newToken;
  }

  public long getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(long expirationTime) {
    this.expirationTime = expirationTime;
  }

  public long getNotBefore() {
    return notBefore;
  }

  public void setNotBefore(long notBefore) {
    this.notBefore = notBefore;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public String getNonce() {
    return nonce;
  }

  public void setNonce(String nonce) {
    this.nonce = nonce;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  private Date getUnixEpochAsDate(long epoch) {

    return new Date(epoch * 1000);
  }

  private boolean isValid(String nonce) {
    Date now = new Date();

    if (now.after(this.getUnixEpochAsDate(this.expirationTime)) ||
        now.before(this.getUnixEpochAsDate(this.notBefore))) {
      return false;
    }

    if (!nonce.equals(this.getNonce())) {
      return false;
    }

    return true;
  }

@Override
public String toString() {
	return "IdToken [expirationTime=" + expirationTime + ", notBefore=" + notBefore + ", tenantId=" + tenantId
			+ ", nonce=" + nonce + ", name=" + name + ", email=" + email + ", preferredUsername=" + preferredUsername
			+ "]";
}

}