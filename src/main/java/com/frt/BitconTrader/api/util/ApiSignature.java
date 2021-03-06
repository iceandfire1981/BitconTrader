package com.frt.BitconTrader.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiSignature {
	final Logger log = LoggerFactory.getLogger(getClass());
	static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
	static final ZoneId ZONE_GMT = ZoneId.of("Z");
	
	public void createSignature(String appKey, String appSecretKey, String method, String uri,
			Map<String, String> params) {
		StringBuilder sb = new StringBuilder(1024);
		int index = uri.indexOf("//");
		String subString = uri.substring(index + 2);
		int index2 = subString.indexOf("/");
		String host = subString.substring(0, index2);
		String constant = subString.substring(index2);
		sb.append(method.toUpperCase()).append('\n') // GET
				.append(host.toLowerCase()).append('\n') // Host
				.append(constant).append('\n'); // /path
		params.remove("Signature");
		params.put("AccessKeyId", appKey);
		params.put("SignatureVersion", "2");
		params.put("SignatureMethod", "HmacSHA256");
		params.put("Timestamp", gmtNow());
		// build signature:
		SortedMap<String, String> map = new TreeMap<>(params);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append('=').append(urlEncode(value)).append('&');
		}
		// remove last '&':
		sb.deleteCharAt(sb.length() - 1);
		// sign:
		Mac hmacSha256 = null;
		try {
			hmacSha256 = Mac.getInstance("HmacSHA256");
			SecretKeySpec secKey = new SecretKeySpec(appSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			hmacSha256.init(secKey);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No such algorithm: " + e.getMessage());
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid key: " + e.getMessage());
		}
		String payload = sb.toString();
		byte[] hash = hmacSha256.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		String actualSign = Base64.getEncoder().encodeToString(hash);
		params.put("Signature", actualSign);

		if (log.isDebugEnabled()) {
			log.debug("Dump parameters:");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				log.debug("  key: " + entry.getKey() + ", value: " + entry.getValue());
			}
		}
	}
	
	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("UTF-8 encoding not supported!");
		}
	}
	
	public long epochNow() {
		return Instant.now().getEpochSecond();
	}
	
	public String gmtNow() {
		return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
	}
}
