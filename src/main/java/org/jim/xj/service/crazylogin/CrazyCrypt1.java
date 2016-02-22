package org.jim.xj.service.crazylogin;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CrazyCrypt1 {
	 protected final Charset charset = Charset.forName("UTF-8");

	 public String encrypt(String name,String password)
	  {
	    String text = "ÜÄaeut//&/=I " + password + "7421€547" + name + "__+IÄIH§%NK " + password;
	    try
	    {
	      MessageDigest md = MessageDigest.getInstance("SHA-512");
	      md.update(text.getBytes(this.charset), 0, text.length());
	      return EncryptHelper.byteArrayToHexString(md.digest());
	    }
	    catch (NoSuchAlgorithmException e) {
	    }
	    return null;
	  }

	  public boolean match(String name, String password, String encrypted)
	  {
	    try
	    {
	      return encrypted.equals(encrypt(name, password));
	    }
	    catch (Exception e) {
	    }
	    return false;
	  }

}
