import ComposeApp
import SwiftUI

struct ContentView: View {
    var body: some View {
        ComposeRootView()
    }
}

private struct ComposeRootView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        IosComposeEntryKt.createLifeCompanionRootViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
