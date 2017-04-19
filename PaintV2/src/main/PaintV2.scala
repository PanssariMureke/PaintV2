package main

import processing.core._

object PaintV2 extends PApplet {
  
  var paintV2: PaintV2 = _
  
  def main(args: Array[String]) = {
    paintV2 = new PaintV2
    val frame = new javax.swing.JFrame("PaintV2")
    frame.getContentPane().add(paintV2)
    paintV2.init
    frame.pack
    frame.setVisible(true)
  }
}

class PaintV2 extends PApplet {
  override def setup = {
    size(1280, 720)
  }
  override def draw = {
    
  }
}