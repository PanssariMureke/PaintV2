package shapes

import processing.core._
import main._

abstract class Shape(val x: Int, val y: Int, val w: Int, val h: Int, p: PApplet, t: PaintV2) {
  
  var borderColor = t.shapeColor    //Yhteinen luokka Rectanglelle ja Ellipselle, tämä kyllä oli ehkä enemmän koodia kuin vain lisätä värimuuttujat suoraan luokkaan
  var fillColor = t.fillColor
  
  def display: Unit
}

class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int, p: PApplet, t: PaintV2) {
  
  var borderColor = t.shapeColor    //Viivoja, luokat tehty lähinnä helpottamaan muotojen tallentamista kokoelmiin
  var fillColor = t.fillColor
  var size = t.penSize
  
  def display = {
    p.strokeWeight(size)
    p.stroke(borderColor)
    p.fill(fillColor)
    p.line(x1, y1, x2, y2)
  }
}

class Rectangle(x: Int, y: Int, w: Int, h: Int, p: PApplet, t: PaintV2) extends Shape(x, y, w, h, p, t) {
  
  def display = {                   //Piirretään kuvio ruudulle
    p.strokeWeight(1)
    p.stroke(borderColor)
    p.fill(fillColor)
    p.rect(x, y, w, h)
  }
}

class Ellipse(x: Int, y: Int, w: Int, h: Int, p: PApplet, t: PaintV2) extends Shape(x, y, w, h, p, t) {
  
  def display = {
    p.ellipseMode(0)
    p.strokeWeight(1)
    p.stroke(borderColor)
    p.fill(fillColor)
    p.ellipse(x, y, w, h)
  }

}

class Text(val x: Int, val y: Int, val text: String, s: Int, p: PApplet, t: PaintV2) {

  var size = s
  var color = t.shapeColor          //Tekstillä on vain täyttöväri
  
  def display = {
    p.fill(color)
    p.textSize(size)
    p.text(text, x, y)
  }
  
}



