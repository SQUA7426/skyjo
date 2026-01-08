// package de.htwg.se.skyjo.aView.Gui
//
// import de.htwg.se.skyjo.aView.Tui
// import de.htwg.se.skyjo.model.{Board, Deck, DiscardPile, Card,toCard}
// import de.htwg.se.skyjo.controller.ControllerComponent.Controller
// import de.htwg.se.skyjo.aView.Gui.{State,fontname,popup, BoardView,Gui}
// import de.htwg.se.skyjo.aView.Gui.Gui.{guiButtons, boardLayer, b}
// import de.htwg.se.skyjo.util.{ConcreteMediator, Mediator, MoveCaretaker}
//
// import org.scalatest.matchers.should.Matchers
// import org.scalatest.wordspec.AnyWordSpec
// import org.scalactic.StringNormalizations._
// import java.io.ByteArrayInputStream
// import scalafx.scene.text.Text
// import scalafx.scene.shape.Rectangle
// import scalafx.scene.layout.StackPane
// import scalafx.scene.layout.HBox
// import scalafx.stage.Stage
// import scalafx.scene.layout.Pane
//
// class GuiTest extends AnyWordSpec with Matchers {
//   "A Gui" when {
//     val brdView = new BoardView()
//     "initialized" should {
//       "be" in:
//         val card9: Card = toCard(brdView._med, 9)
//         val cV = brdView.CardView(
//           x_pos = 0,
//           y_pos = 0,
//           cCard = card9,
//           switchDeckDisc = () => {},
//           switchDeckB = () => {},
//           switchDiscB = () => {},
//           endTurn = () => {}
//         )
//         fontname shouldBe("Parisienne")
//         // popup shouldBe a[Unit]
//         currentState shouldBe a[State]
//         currentState.nextState() shouldBe a[State]
//         State.BEGIN shouldBe a[State]
//         State.MID shouldBe a[State]
//         State.END shouldBe a[State]
//
//         brdView._med shouldBe a[Mediator]
//         brdView.padding shouldBe 30
//         brdView.aDeck shouldBe a[Deck]
//         brdView.termBoard shouldBe a[Board]
//         brdView.aDisc shouldBe a[DiscardPile]
//
//         brdView.memStack shouldBe a[MoveCaretaker]
//
//         cV.x_pos shouldBe 0
//         cV.y_pos shouldBe 0
//         cV.h shouldBe 198
//         cV.w shouldBe 132
//         cV.turned shouldBe false
//         cV.med shouldBe a[Mediator]
//         cV.isDisc shouldBe false
//         cV.isDeck shouldBe false
//         cV.switchDeckDisc
//         cV.switchDeckB
//         cV.switchDiscB
//         cV.endTurn
//         cV.arcH shouldBe 30
//         cV.arcW shouldBe 30
//         cV.label shouldBe a[Text]
//         cV.selected shouldBe false
//         cV.toString() shouldBe a[String]
//
//         cV.cardShape shouldBe a[Rectangle]
//         cV.createLabel shouldBe a[Text]
//
//         cV.uptCardView shouldBe a[Unit]
//         cV.view shouldBe a[StackPane]
//
//         brdView.BOARD_INIT() shouldBe a[Seq[brdView.CardView]]
//         brdView.BOARD_INIT(false) shouldBe a[Seq[brdView.CardView]]
//         brdView.manyCards shouldBe a[Seq[brdView.CardView]]
//
//         brdView.viewBoard() shouldBe a[Seq[brdView.CardView]]
//         brdView.aDiscard shouldBe a[brdView.CardView]
//         brdView.viewDisc() shouldBe a[StackPane]
//         brdView.vDeck shouldBe a[brdView.CardView]
//         brdView.viewDeck() shouldBe a[StackPane]
//         brdView.vDeck shouldBe a[brdView.CardView]
//
//         currentState shouldBe State.BEGIN
//         currentState = currentState.nextState()
//         currentState shouldBe State.MID
//
//         currentState = currentState.nextState()
//         currentState shouldBe State.END
//
//         // val tmpStage = new Stage()
//         // guiButtons(tmpStage) shouldBe a[HBox]
//         boardLayer shouldBe a[Pane]
//         b shouldBe a[BoardView]
//     }
//   }
// }
