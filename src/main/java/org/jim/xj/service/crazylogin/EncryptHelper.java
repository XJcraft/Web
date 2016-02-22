package org.jim.xj.service.crazylogin;


public final class EncryptHelper
{
  private static final char[] CRYPTCHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
 

  public static String byteArrayToHexString(byte[] args)
  {
    char[] chars = new char[args.length * 2];
    for (int i = 0; i < args.length; i++)
    {
      chars[(i * 2)] = CRYPTCHARS[(args[i] >> 4 & 0xF)];
      chars[(i * 2 + 1)] = CRYPTCHARS[(args[i] & 0xF)];
    }
    return new String(chars);
  }
}