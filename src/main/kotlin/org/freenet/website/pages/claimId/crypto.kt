package org.freenet.website.pages.claimId


import com.google.gson.Gson
import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPair
import java.util.Base64
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

fun RSAPublicKey.encodeToPem(): String {
    val publicKeyPEM = StringBuilder()
    val encoder = Base64.getMimeEncoder(64, "\n".toByteArray()) // MIME encoder will wrap lines

    // Wrap the public key in the appropriate PEM header and footer
    publicKeyPEM.append("-----BEGIN PUBLIC KEY-----\n")
    publicKeyPEM.append(String(encoder.encode(this.encoded)))
    publicKeyPEM.append("\n-----END PUBLIC KEY-----\n")

    return publicKeyPEM.toString()
}

object RSASigner {

    data class RSAKeyPair(val public: String, val private: String)
    object FreenetKey {
        lateinit var rsaKey: RSAKeyPair

        fun initialize() {
            // Load the json with the RSA keys from an environment variable
            val jsonContent = String(Files.readAllBytes(Paths.get("src/main/resources/rsa_sample_key.json")))

            val keys = Gson().fromJson(jsonContent, RSAKeyPair::class.java)

            val rsaKeyPair = RSAKeyPair(keys.public, keys.private)

            rsaKey = rsaKeyPair

            println(rsaKey.public)
            println(rsaKey.private)

        }
    }

    fun RSAPrivateKey.toPEM(): String {
        val privateKeyPEM = StringBuilder()
        val encoder = Base64.getEncoder()

        // Wrap the private key in the appropriate PEM header and footer
        privateKeyPEM.append("-----BEGIN PRIVATE KEY-----\n")
        privateKeyPEM.append(encoder.encodeToString(this.encoded))
        privateKeyPEM.append("\n-----END PRIVATE KEY-----\n")

        return privateKeyPEM.toString()
    }



    fun toFreenetRSAPublicKey(pem: String): RSAPublicKey {
        if (!pem.contains("-----BEGIN PUBLIC KEY-----") || !pem.contains("-----END PUBLIC KEY-----")) {
            throw IllegalArgumentException("String does not contain valid public key PEM")
        }

        val publicKeyPEM = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")

        val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPEM))
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePublic(keySpecX509) as RSAPublicKey
    }

    fun toFreenetRSAPrivateKey(pem: String): RSAPrivateKey {
        if (!pem.contains("-----BEGIN PRIVATE KEY-----") || !pem.contains("-----END PRIVATE KEY-----")) {
            throw IllegalArgumentException("String does not contain valid private key PEM")
        }

        val privateKeyPEM = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\n", "")

        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPEM))
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpecPKCS8) as RSAPrivateKey
    }

    //Performs an RSA blind signature on the client's blinded key
    fun RSASign(message: String): String {
        val privateSignature = Signature.getInstance("SHA256withRSA")
        val privateKey = toFreenetRSAPrivateKey(FreenetKey.rsaKey.private)
        privateSignature.initSign(privateKey)

        privateSignature.update(message.toByteArray())

        val signature = privateSignature.sign()

        val signatureString = Base64.getEncoder().encodeToString(signature)
        return signatureString
    }

}