import ComposeApp
import SwiftUI

@main
struct LifeCompanionApp: App {
    init() {
        IosComposeEntryKt.initializeLifeCompanionKoinForIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
