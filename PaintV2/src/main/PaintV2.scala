package main

import processing.core._
import button._

object PaintV2 extends PApplet {
  
  var paintV2: PaintV2 = _
  var sW: SecondWindow = _
  
  def main(args: Array[String]) = {
    paintV2 = new PaintV2
    sW = new SecondWindow
    val frame = new javax.swing.JFrame("PaintV2")
    val frame2 = new javax.swing.JFrame("Tools")
    frame.getContentPane().add(paintV2)
    frame2.getContentPane().add(sW)
    paintV2.init
    sW.init
    frame.pack
    frame2.pack
    frame.setVisible(true)
    frame2.setVisible(true)
  }
}

class PaintV2 extends PApplet {
  
  var buttons: Array[RectangleButton] = Array()
  private val butOff = color(255)
  private val butOn = color(193, 224, 255)
  private val mock = {}
  buttons = buttons:+ new RectangleButton("Uusi", 2, 2, 40, 20, butOff, butOn, this, mock)
  buttons = buttons:+ new RectangleButton("Tallenna", 44, 2, 40, 20, butOff, butOn, this, mock)
  
  override def setup = {
    size(1280, 720)
    background(255)
  }
  override def draw = {
    for (button <- buttons) {
      button.update
      button.display
    }
  }
  
  override def mouseClicked = {
    
  }
}

class SecondWindow extends PApplet {
  override def setup = {
    size(200, 400)
  }
  override def draw = {
    
  }
}


