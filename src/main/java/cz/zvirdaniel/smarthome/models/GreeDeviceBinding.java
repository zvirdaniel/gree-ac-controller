package cz.zvirdaniel.smarthome.models;

import cz.zvirdaniel.smarthome.utils.CryptoUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.GregorianCalendar;

@Data
@RequiredArgsConstructor
public class GreeDeviceBinding {
	private final GreeDevice device;
	private final String aesKey;
	private final Date creationDate = GregorianCalendar.getInstance().getTime();

	public String decryptContent(String encryptedContent) {
		return CryptoUtil.decryptContent(this.getAesKey(), encryptedContent);
	}

	public String encryptContent(String content) {
		return CryptoUtil.encryptContent(this.getAesKey(), content);
	}
}
