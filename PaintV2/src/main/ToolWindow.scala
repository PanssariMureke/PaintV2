package main

import processing.core._
import javax.swing.JOptionPane._
import button._
import scala.math._

class ToolWindow extends PApplet {
  
  var toolButtons: Array[RectangleButton] = Array()    //Aletaan luomaan nappuloita
  private val butOff = color(255)
  private val butOn = color(193, 224, 255)
  private def setStatusPressed = {                     //Yksinkertainen metodi, jolla asetetaan napin tila painetuksi ja muut napit ei painetuiksi
    for (button <- toolButtons) {
      button.pressed = false
    }
    for (button <- toolButtons) {
      if (button.over) button.pressed = true
    }
  }
  private def setStatusPressedPen: Unit = {            //Sama metodi kynälle, mutta kysytään myös painalluksen yhteydessä kuinka paksun viivan haluaa
    for (button <- toolButtons) {
      button.pressed = false
    }
    pen.pressed = true
    val s = showInputDialog("Valitse viivan paksuus!")
    try {
      penSize = s.toInt
    } catch {
      case toInt: NumberFormatException => { println("Oops, that didn't work, using default pen size!"); penSize = 1 }
    }
  }
  val pen = RectangleButton(loadImage("assets/Pen-icon.png"), 2, 2, 40, 40, butOff, butOn, this, setStatusPressedPen)
  val write = RectangleButton(loadImage("assets/Letter-A-icon.png"), 44, 2, 40, 40, butOff, butOn, this, setStatusPressed)
  val rectangle = RectangleButton(loadImage("assets/Rectangle-icon.png"), 2, 44, 40, 40, butOff, butOn, this, setStatusPressed)
  val circle = RectangleButton(loadImage("assets/Circle-icon.png"), 44, 44, 40, 40, butOff, butOn, this, setStatusPressed)
  val ellipse = RectangleButton(loadImage("assets/Ellipse-icon.png"), 86, 44, 40, 40, butOff, butOn, this, setStatusPressed)
  toolButtons = toolButtons:+ pen                      //Luodaan nappulat
  toolButtons = toolButtons:+ write
  toolButtons = toolButtons:+ rectangle
  toolButtons = toolButtons:+ circle
  toolButtons = toolButtons:+ ellipse
  
  private val colorPicker = new ColorPicker(5, 115, 190, 190, 0, this) //Luodaan uusi värinvalitsin, sen luokka on määritelty alla
  
  var shapeColor = color(0)                            //Värit ja kynän koko haetaan täältä
  var fillColor = color(255)
  var penSize = 1
  
  override def setup = {
    size(200, 310)
  }
  
  override def draw = {
    background(255)                                    //Lähdetään tyhjästä ruudusta liikkeelle
    for (button <- toolButtons) {                      //Päivitetään siihen nappulat
      button.update
      button.display
    }
    fill(255)
    stroke(255)
    rect(86, 2, 100, 42)
    rect(128, 42, 100, 42)
    rect(2, 84, 250, 60)
    
    colorPicker.display                                //Päivitetään siihen värinvalitsin
    shapeColor = colorPicker.strokeColor               //Päivitetään värit
    fillColor = colorPicker.fillColor
    fill(shapeColor)                                   //Lisätään pienet sievät ruudut, joista näkee valitun värin
    rect(5, 100, 93, 10)
    fill(fillColor)
    rect(100, 100, 93, 10)
  }
  
  override def mouseReleased = {
    for (button <- toolButtons) {                      //Kun hiiri päästetään ylös, tarkistetaan haluttiinko painaa jotain nappulaa
      if (button.over) {
        button.press
      }
    }
  }
  
}



                                                       //Kiitokset matikasta Processing-forumien käyttäjälle julapy
class ColorPicker(x: Int, y: Int, width: Int, height: Int, var strokeColor: Int, p: PApplet) {
  private val cpImage: PImage = new PImage(width, height)      //Tehdään uusi PImage, johon muokataan sitten halutut pikselit 
  var fillColor = p.color(255)
  init
  
  private def init = {                                 //Käytetään paljon matematiikkaa ja tehdään väriliu'ut halutun kokoisena
    val cw = width-40
    for (i <- 0 until cw) {
      val nColorPercent = i/(cw.toDouble)
      val rad = (-360*nColorPercent)*(math.Pi/180)
      val nR = (math.cos(rad)*127+128).toInt << 16
      val nG = (math.cos(rad+2*math.Pi/3)*127+128).toInt << 8
      val nB = (math.cos(rad+4*math.Pi/3)*127+128).toInt
      val nColor = nR | nG | nB                        
      setGradient(i, 0, 1, height/2, 0xFFFFFF, nColor) //Sarakkeittain piirretään liu'ut värin ja valkoisen tai mustan välillä
      setGradient(i, height/2, 1, height/2, nColor, 0x000000)
    }
    
    for (j <- 0 until height) {                        //Piirretään viereen liuku valkoisesta mustaan harmaan sävyjen valitsemista varten
      val g = (255-(j.toDouble/(height-1).toDouble*255)).toInt
      drawRect(width-30, j, 30, 1, p.color(g))
    }
  }
  
  private def setGradient(x: Int, y: Int, w: Double, h: Double, c1: Int, c2: Int) = {
    val deltaR: Double = (c2 >> 16 & 0xFF)-(c1 >> 16 & 0xFF)
    val deltaG: Double = (c2 >> 8 & 0xFF)-(c1 >> 8 & 0xFF)
    val deltaB: Double = (c2 & 0xFF)-(c1 & 0xFF)
    var i = y                                          //Apufunktio väriliu'un tekemiseen
    while (i < y.toDouble+h) {
      val c = p.color(((c1 >> 16 & 0xFF)+(i-y)*(deltaR/h)).toFloat, ((c1 >> 8 & 0xFF)+(i-y)*(deltaG/h)).toFloat, ((c1 & 0xFF)+(i-y)*(deltaB/h)).toFloat)
      cpImage.set(x, i, c)
      i+=1
    }
  }
  
  private def drawRect(rx: Int, ry: Int, rw: Int, rh: Int, rc: Int) = {
    for (i <- rx until rx+rw) {                        //Yksinkertainen apufunktio neliskulmion piirtämiseen kuvaan
      for (j <- ry until ry+rh) {
        cpImage.set(i, j, rc)
      }
    }
  }

  def display = {
    p.image(cpImage, x, y)                              //Piirretään kuva ja ympäröidään osiot viivoilla
    p.stroke(0)
    p.strokeWeight(1)
    p.line(x-1, y-2, x+width-40, y-2)
    p.line(x-1, y-2, x-1, y+height)
    p.line(x+width-40, y-2, x+width-40, y+height)
    p.line(x-1, y+height, x+width-40, y+height)
    p.line(x+width-31, y-2, x+width, y-2)
    p.line(x+width-31, y-2, x+width-31, y+height)
    p.line(x+width, y-2, x+width, y+height)
    p.line(x+width-31, y+height, x+width, y+height)
    if (p.mousePressed && p.mouseButton==37 && p.mouseX>=x && p.mouseX<x+width && p.mouseY>=y && p.mouseY<y+height) {
      strokeColor = p.get(p.mouseX, p.mouseY)
    }
    else if (p.mousePressed && p.mouseButton==39 && p.mouseX>=x && p.mouseX<x+width && p.mouseY>=y && p.mouseY<y+height) {
      fillColor = p.get(p.mouseX, p.mouseY)             //Tarkkaillaan, jos halutaan vaihtaa väriä, hiiren vasen nappula on piirtovärille ja oikea täyttövärille
    }
  }
}
