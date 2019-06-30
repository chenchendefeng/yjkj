package com.jiayi.platform.basic.util;

public class FnvHash {
	private static final long FNV_64_INIT = 0xcbf29ce484222325L;
	private static final long FNV_64_PRIME = 0x100000001b3L;
	
  public static long fnv1Hash64(String k) {
		long rv = FNV_64_INIT;
		int len = k.length();
		for (int i = 0; i < len; i++) {
			rv *= FNV_64_PRIME;
			rv ^= k.charAt(i);
		}
		return rv;
	}
  
  public static long fnv1aHash64(String k) {
  	long rv = FNV_64_INIT;
		int len = k.length();
		for (int i = 0; i < len; i++) {
			rv ^= k.charAt(i);
			rv *= FNV_64_PRIME;
		}
		return rv;
  }
}
