
import java.security.MessageDigest;

class CryptoService {
	
	boolean transactional = false

	def sha1(byte[] src) {
		def md = MessageDigest.getInstance("SHA1");
		def dig = md.digest(src);
		return dig.encodeBase64();
	}
	

}

