// import de.htwg.se.skyjo.util
// import
//
// trait Handler {
//   var next: Handler = null
//   def handle(request: String): Unit = {
//     if (next != null) next.handle(request)
//     else println(s"No handler for: $request")
//   }
// }
//
// class DiscHandler extends Handler {
//   override def handle(request: String) {
//     if request.compareTo("0") then
//   }
// }
// class DeckHandler extends Handler {
//   override def handle(request: String) {
//     if request.compareTo("1") then
//   }
// }
//
//
// object SupportHandler extends Handler {
//   val discHandler = new DiscHandler
//   val deckHandler = new DeckHandler
//
//   discHandler.next = DeckHandler

  // Test requests
  // discHandler.handle("123")
  // discHandler.handle("abc")
  // discHandler.handle("abc123")
// }
