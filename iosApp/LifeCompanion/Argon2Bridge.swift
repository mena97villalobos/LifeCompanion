import Argon2Swift
import ComposeApp
import Foundation

/// Swift implementation of the Kotlin `IosArgon2` interface, backed by the `Argon2Swift` SPM package.
///
/// Produces and verifies PHC-encoded Argon2id strings (`$argon2id$v=19$m=...`). Parameters mirror the
/// Android side (`AndroidArgon2PinHasher`): 3 iterations, 64 MiB, parallelism 1, 32-byte hash.
final class Argon2Bridge: IosArgon2 {
    private let iterations = 3
    private let memoryKiB = 65_536
    private let parallelism = 1
    private let hashLength = 32
    private let saltLength = 16

    func hashEncoded(password: String) -> String {
        do {
            let salt = Salt.newSalt(length: saltLength)
            let result = try Argon2Swift.hashPasswordString(
                password: password,
                salt: salt,
                iterations: iterations,
                memory: memoryKiB,
                parallelism: parallelism,
                length: hashLength,
                type: .id
            )
            return result.encodedString()
        } catch {
            // An empty hash makes every subsequent verify() fail, so a corrupt PIN never validates.
            return ""
        }
    }

    func verify(password: String, encoded: String) -> Bool {
        guard !encoded.isEmpty else { return false }
        return (try? Argon2Swift.verifyHashString(password: password, hash: encoded, type: .id)) ?? false
    }
}
