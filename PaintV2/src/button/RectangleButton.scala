package button

import processing.core._

object RectangleButton {
  def apply(logo: PImage, x: Int, y: Int, sizeX: Int, sizeY: Int, baseColor: Int, highlightColor: Int, p: PApplet, pressFunction: => Unit) = {
    new RectangleButton(logo, x, y, sizeX, sizeY, baseColor, highlightColor, p, pressFunction)
  }
  def apply(name: String, x: Int, y: Int, sizeX: Int, sizeY: Int, baseColor: Int, highlightColor: Int, p: PApplet, pressFunction: => Unit) = {
    new RectangleButton(name, x, y, sizeX, sizeY, baseColor, highlightColor, p, pressFunction)
  }                                                 //Apply-funktio että voi helposti tehdä nappuloita, joissa on joko teksti tai kuva
}

class RectangleButton(val thing: Any, val x: Int, val y: Int, val sizeX: Int, val sizeY: Int, val baseColor: Int, val highlightColor: Int, 
                      val p: PApplet, pressFunction: => Unit) extends Button {
  
  var currentColor = baseColor                      //Värit, onko nappula painettu ja mikä kuva sillä on, jos on
  var pressed = false
  private var photo: PImage = _
  
  if (thing.isInstanceOf[PImage]) {                 //Jos nappulaa aluodessa annettiin kuva, niin skaalataan se nappulan kokoiseksi
    photo = thing.asInstanceOf[PImage]
    photo.resize(sizeX-2, sizeY-2)
  }
  
  def over = {                                      //Tarkistetaan onko hiiri nappulan päällä
    if (p.mouseX >= x && p.mouseX <= x+sizeX &&
        p.mouseY >= y && p.mouseY <= y+sizeY) {
      true
    } else false
  }

  def press = {                                      //Jos nappulaa painetaan niin suoritetaan sille välitetty funktio
    pressFunction
  }
  
  def display = {                                    //Piirretään nappula ruudulle
    p.fill(currentColor)
    p.noStroke
    p.rect(x, y, x+sizeX, y+sizeY)
    p.fill(0)
    if (thing.isInstanceOf[String]) {p.textSize(15); p.text(thing.toString, x+5, (y+sizeY)/2+8) }
    else if (thing.isInstanceOf[PImage]) p.image(photo, x+2, y+2)
  }
}
