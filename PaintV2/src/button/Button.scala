package button

abstract class Button {
  
  val x, y: Int                          //Yhteinen luokka nappuloille, lähinnä tämä nyt on jos haluaisi tehdä erimuotoisia nappuloita
  val sizeX: Int
  val sizeY: Int
  val baseColor, highlightColor: Int
  var currentColor: Int
  var pressed: Boolean
  
  def update = {
    if (over || pressed) currentColor = highlightColor
    else currentColor = baseColor
  }
  
  def over: Boolean
  
  def press: Unit
  
  def display: Unit
}