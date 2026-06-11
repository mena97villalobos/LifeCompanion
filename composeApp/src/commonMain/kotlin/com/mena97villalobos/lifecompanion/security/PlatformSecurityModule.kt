package com.mena97villalobos.lifecompanion.security

import org.koin.core.module.Module

/**
 * Platform-provided security bindings for the Koin graph.
 *
 * - **Android**: binds [com.mena97villalobos.domain.security.BiometricAuthenticator] to the
 *   `BiometricPrompt`-backed implementation.
 * - **iOS**: binds the `LAContext`-backed authenticator. (The Argon2id PIN hasher is bound from the
 *   iOS app entry point, since it is supplied by the Swift `Argon2Swift` SPM package.)
 */
expect val platformSecurityModule: Module
