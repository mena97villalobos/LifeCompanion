package com.mena97villalobos.remote.minio

import com.mena97villalobos.remote.crypto.PlatformCrypto
import com.mena97villalobos.remote.crypto.toHexLower

internal object AwsSigV4Signer {
    private const val ALGORITHM = "AWS4-HMAC-SHA256"
    private const val SERVICE = "s3"
    private const val AWS4_REQUEST = "aws4_request"

    fun authorizationHeaderForPut(
        method: String,
        canonicalUri: String,
        hostHeader: String,
        bodyHashHex: String,
        amzDate: String,
        dateStamp: String,
        region: String,
        accessKey: String,
        secretKey: String,
    ): String {
        val canonicalHeaders = buildString {
            append("host:")
            append(hostHeader)
            append('\n')
            append("x-amz-content-sha256:")
            append(bodyHashHex)
            append('\n')
            append("x-amz-date:")
            append(amzDate)
            append('\n')
        }
        val signedHeaders = "host;x-amz-content-sha256;x-amz-date"
        val canonicalRequest = buildString {
            append(method)
            append('\n')
            append(canonicalUri)
            append('\n')
            append('\n')
            append(canonicalHeaders)
            append('\n')
            append(signedHeaders)
            append('\n')
            append(bodyHashHex)
        }
        val credentialScope = "$dateStamp/$region/$SERVICE/$AWS4_REQUEST"
        val canonicalRequestHash =
            PlatformCrypto.sha256(canonicalRequest.encodeToByteArray()).toHexLower()
        val stringToSign = buildString {
            append(ALGORITHM)
            append('\n')
            append(amzDate)
            append('\n')
            append(credentialScope)
            append('\n')
            append(canonicalRequestHash)
        }
        val signingKey = getSignatureKey(secretKey, dateStamp, region, SERVICE)
        val signature =
            PlatformCrypto.hmacSha256(signingKey, stringToSign.encodeToByteArray()).toHexLower()
        return "$ALGORITHM Credential=$accessKey/$credentialScope, SignedHeaders=$signedHeaders, Signature=$signature"
    }

    private fun getSignatureKey(key: String, dateStamp: String, region: String, service: String): ByteArray {
        var k = PlatformCrypto.hmacSha256(("AWS4$key").encodeToByteArray(), dateStamp.encodeToByteArray())
        k = PlatformCrypto.hmacSha256(k, region.encodeToByteArray())
        k = PlatformCrypto.hmacSha256(k, service.encodeToByteArray())
        return PlatformCrypto.hmacSha256(k, AWS4_REQUEST.encodeToByteArray())
    }
}
