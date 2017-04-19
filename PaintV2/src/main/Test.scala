package main

import processing.core._
 
object Test extends PApplet {
 
  private var test:Test = _
 
  def main(args: Array[String]) = {
    test = new Test
    val frame = new javax.swing.JFrame("Test")
    frame.getContentPane().add(test)
    test.init
    frame.pack
    frame.setVisible(true)
  }
}
 
class Test extends PApplet {
  override def setup() = {
    
  }
  
  override def draw() = {
    
  }
}