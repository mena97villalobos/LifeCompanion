import ComposeApp
import SwiftUI

@main
struct LifeCompanionApp: App {
    init() {
        IosComposeEntryKt.initializeLifeCompanionKoinForIos(argon2: Argon2Bridge())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
