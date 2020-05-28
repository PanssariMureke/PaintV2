package main

import processing.core._
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.JOptionPane._
import javax.swing.JTextField
import java.io.PrintWriter
import scala.io.Source
import scala.math._
import button._
import shapes._

object PaintV2 {
  
  var paintV2: PaintV2 = _
  var tW: ToolWindow = _
  
  def main(args: Array[String]) = {                      //Processingin vaatima main, luodaan ikkunat
    tW = new ToolWindow
    val frame2 = new javax.swing.JFrame("Tools")
    frame2.getContentPane().add(tW)
    tW.init
    frame2.setResizable(false)
    frame2.pack
    frame2.setVisible(true)
    paintV2 = new PaintV2(tW)
    val frame = new javax.swing.JFrame("PaintV2")
    frame.getContentPane().add(paintV2)
    paintV2.init
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
    frame.setPreferredSize(new java.awt.Dimension(1280, 720))
    frame.pack
    frame.setVisible(true)
  }
}

class PaintV2(tW: ToolWindow) extends PApplet {
  
  private var tools = tW
  var buttons: Array[RectangleButton] = Array()       //Aletaan luomaan yläreunan nappuloita
  private val butOff = color(255)
  private val butOn = color(193, 224, 255)
 
  private def uusi = {
    shapes = Array()                                  //Metodit, jotka annetaan nappuloille suoritettavaksi kun niitä kutsutaan
    redoArray = Array()
    backgroundImage = (false, createImage(1280, 695, 1))
    background(255)
  }
  private def undo = {
    if (!shapes.isEmpty) {                            
      redoArray = redoArray:+shapes.last
      shapes = shapes.dropRight(1)
    }
  }
  private def redo = {
    if (!redoArray.isEmpty) {
      shapes = shapes:+redoArray.last
      redoArray = redoArray.dropRight(1)
    }
  }
  private def tallenna = {
    val fc = new JFileChooser
    fc.setAcceptAllFileFilterUsed(false)
    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt", "txt"))
    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.png", "png"))
    var path = ""                                    //Käytetään tallennuspaikan ja nimen valitsemiseen Java swingin JFileChooser luokkaa yksinkertaisuuden vuoksi
    val returnVal = fc.showSaveDialog(this)          //Sillä saa kätevästi halutun tiedoston ja paikan absoluuttisen polun, sekä rajattua tiedostomuotoja
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      path = fc.getSelectedFile.getAbsolutePath
      if(path.takeRight(4)==".txt") {                //Path on yleensä muotoa C:/Users/*user*/Desktop/tiedosto.txt tai /home/*user*/Desktop/tiedosto.txt riippuen 
        val file = new PrintWriter(path)             //järjestelmästä, joten viimeisten merkkien avulla voi helposti tarkistaa miten kuva halutaan tallennettavan
        try {
          file.println(backgroundImage._1)           //Tallennetaan taustakuva viereen samannimisenä .png-tiedostona
          if(backgroundImage._1) {
            backgroundImage._2.save(path.take(path.length-4)+".png")
          }
          for (shape <- shapes) {
            if (shape.isInstanceOf[Array[Line]]) {   //Käyriä viivoja käsitellään Arrayna suoria viivoja, tallennetaan siis värit ja jokaisen viivan alku- ja loppupiste
              val arr = shape.asInstanceOf[Array[Line]]
              file.println("Line")
              file.println(arr(0).borderColor)
              file.println(arr(0).fillColor)
              file.println(arr(0).size)
              for (o <- arr) {
                file.println(s"${o.x1},${o.y1},${o.x2},${o.y2}")
              }
              file.println("LineEnd")
            }
            else if (shape.isInstanceOf[Rectangle]) {//Rectanglesta riittää tallentaa värit ja vasemman ylänurkan koordinaatit sekä leveys ja korkeus
              val r = shape.asInstanceOf[Rectangle]
              file.println("Rectangle")
              file.println(r.borderColor)
              file.println(r.fillColor)
              file.println(s"${r.x},${r.y},${r.w},${r.h}")
            }
            else if (shape.isInstanceOf[Ellipse]) {  //Ellipsen kohdalla tallennetaan samat tiedot kuin Rectanglella. Ympyrät ovat myös Ellipsejä
              val e = shape.asInstanceOf[Ellipse]
              file.println("Ellipse")
              file.println(e.borderColor)
              file.println(e.fillColor)
              file.println(s"${e.x},${e.y},${e.w},${e.h}")
            }
            else if (shape.isInstanceOf[Text]) {
              val t = shape.asInstanceOf[Text]
              file.println("Text")
              file.println(t.color)
              file.println(t.size)
              file.println(s"${t.x},${t.y}")
              file.println(t.text)
            }
          }
        } finally {
          file.close                                 //Vapautetaan lopuksi resurssit nätisti
        }
      } else if(path.takeRight(4)==".png") {
        val image = get(0, 26, width, height-26)
        image.save(path)                             //Jos halutaan tallentaa .png-formaatissa, niin annetaan processingin valmiin save-metodin tehdä työt
      } else {
        JOptionPane.showMessageDialog(null, "Tiedoston tallennus epäonnistui! Huom: Tallennus tarvitsee tiedostopäätteen .txt tai .png!")
      }                                              //Path tarvitsee tiedostopäätteen ja se puuttuu jos käyttäjä ei sitä anna. Vaikea tietää mihin muotoon 
    }                                                //käyttäjä haluaa tiedoston, joten ei tallenneta mitään ja ilmoitetaan asiasta 
  }
  private def lataa = {
    val fc = new JFileChooser
    fc.setAcceptAllFileFilterUsed(false)             //Mahdollista ladata .txt-tiedostoja tai .png-kuvia, jotka asettuvat taustaksi ja joiden päälle voi piirtää
    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.txt", "txt"))
    fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("*.png", "png"))
    var path = ""
    val returnVal = fc.showOpenDialog(this)
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      path = fc.getSelectedFile.getAbsolutePath
      if(path.takeRight(4) == ".txt") {
        uusi                                           //Tyhjennetään kenttä ja aloitetaan lataamaan muotoja rivi kerrallaan  
        val file = Source.fromFile(path)
        try {
          val fileL = file.getLines()
          if(fileL.next=="true") {
            backgroundImage = (true, loadImage(path.take(path.length-4)+".png"))
            image(backgroundImage._2, 0, 25)
          }
          for (line <- fileL) {
            if (line == "Line") {
              shapeColor = fileL.next.toInt
              fillColor = fileL.next.toInt
              penSize = fileL.next.toInt
              var l = fileL.next
              var a = Array[Line]()
              do {
                val x1 = l.takeWhile { _!=',' }
                val y1 = l.drop(x1.length+1).takeWhile { _!=',' }
                val x2 = l.drop(x1.length+y1.length+2).takeWhile { _!=',' }
                val y2 = l.drop(x1.length+y1.length+x2.length+3).takeWhile { _!=',' }
                a = a:+new Line(x1.toInt, y1.toInt, x2.toInt, y2.toInt, this, this)
                l = fileL.next
              } while (l != "LineEnd")
              shapes = shapes:+a
              draw                                     //Jokaisen valmiin muodon jälkeen lisätään se piirrettäväksi ja päivitetään ikkuna, että muotojen värit menisivät oikein
            }
            else if (line == "Rectangle") {
              shapeColor = fileL.next.toInt
              fillColor = fileL.next.toInt
              val l = fileL.next
              val x = l.takeWhile { _!=',' }
              val y = l.drop(x.length+1).takeWhile { _!=',' }
              val w = l.drop(x.length+y.length+2).takeWhile { _!=',' }
              val h = l.drop(x.length+y.length+w.length+3).takeWhile { _!=',' }
              shapes = shapes:+new Rectangle(x.toInt, y.toInt, w.toInt, h.toInt, this, this)
              draw
            }
            else if (line == "Ellipse") {
              shapeColor = fileL.next.toInt
              fillColor = fileL.next.toInt
              val l = fileL.next
              val x = l.takeWhile { _!=',' }
              val y = l.drop(x.length+1).takeWhile { _!=',' }
              val w = l.drop(x.length+y.length+2).takeWhile { _!=',' }
              val h = l.drop(x.length+y.length+w.length+3).takeWhile { _!=',' }
              shapes = shapes:+new Ellipse(x.toInt, y.toInt, w.toInt, h.toInt, this, this)
              draw
            }
            else if (line == "Text") {
              fillColor = fileL.next.toInt
              val s = fileL.next
              val l = fileL.next
              val x = l.takeWhile { _!=',' }
              val y = l.drop(x.length+1).takeWhile { _!=',' }
              val t = fileL.next
              shapes = shapes:+new Text(x.toInt, y.toInt, t, s.toInt, this, this)
              draw
            }
          }
        } catch {
          case toInt: NumberFormatException => println("Oops! toInt didn't work, something is probably wrong with the file!")
          case nullP: NullPointerException => {
            println("Something went wrong! Most probably backgroundimage was not found. Have you removed it? Bring it back and try again!")
            backgroundImage = (false, createImage(1280, 695, 1)) 
          }
          case _: Throwable => println("Okay, I apparently fucked up! But sshh, don't tell anyone!")
        } finally {
          file.close                                   //Vapautetaan resurssit lopuksi nätisti
        }
      } else if (path.takeRight(4) == ".png") {        //Mahdollisuus ladata myös .png-kuvia
        uusi
        backgroundImage = (true, loadImage(path))
      } else println("nyt meni vituiks")
    } 
  }
  
  buttons = buttons:+ new RectangleButton("Uusi", 2, 2, 40, 20, butOff, butOn, this, uusi)
  buttons = buttons:+ new RectangleButton("Tallenna", 44, 2, 70, 20, butOff, butOn, this, tallenna)
  buttons = buttons:+ new RectangleButton("Lataa", 116, 2, 50, 20, butOff, butOn, this, lataa)
  buttons = buttons:+ new RectangleButton("Undo", 168, 2, 50, 20, butOff, butOn, this, undo)
  buttons = buttons:+ new RectangleButton("Redo", 220, 2, 50, 20, butOff, butOn, this, redo)
  buttons = buttons:+ new RectangleButton("Lopeta", 272, 2, 60, 20, butOff, butOn, this, System.exit(0))
                                                     //Tehdään viimein nappulat yläreunaan
  private var backgroundImage = (false, createImage(1280, 695, 1))
  private var fmouseX = mouseX                       //Muuttujat johon tallennetaan hiiren sijainti sillä hetkellä, kun jokin hiiren nappula painetaan alas
  private var fmouseY = mouseY
  
  private var line: Array[Line] = Array()
  private var rectangle: Rectangle = new Rectangle(0, 0, 0, 0, this, this)
  private var ellipse: Ellipse = new Ellipse(0, 0, 0, 0, this, this)
  private var circle: Ellipse = new Ellipse(0, 0, 0, 0, this, this)  //Tilapäiset säilytyspaikat piirrossa oleville viivoille tai muille muodoille
  private var shapes: Array[Object] = Array()        //Taulukko, jossa on näkyvät muodot ja josta ne piirretään
  private var redoArray: Array[Object] = Array()     //Ei hävitetä muotoja, jos ne poistetaan undolla
  
  var shapeColor = tools.shapeColor                  //Värit ja kynän koko
  var fillColor = tools.fillColor
  var penSize = tools.penSize
  
  
  override def setup = {
    background(255)
    size(1280, 720)
  }
  
  override def draw = {                              //Metodi joka ajetaan jokaisen ruudunpäivityksen yhteydessä, eli 60 kertaa sekunnissa
    background(255)  
    if(backgroundImage._1) {
      image(backgroundImage._2, 0, 25)
    }                               
                                                     //Aloitetaan tyhjällä valkoisella taustalla
    if (mousePressed) {                              //Jatketaan katsomalla haluaako käyttäjä lisätä sillä hetkellä uutta muotoa
      if (mouseY>25) {
        if (tools.pen.pressed) {
          line = line:+ new Line(pmouseX, pmouseY, mouseX, mouseY, this, this)
        }
        else if (tools.rectangle.pressed) {
          rectangle = new Rectangle(fmouseX, fmouseY, mouseX-fmouseX, mouseY-fmouseY, this, this)
        }
        else if (tools.ellipse.pressed) {
          ellipse = new Ellipse(fmouseX, fmouseY, mouseX-fmouseX, mouseY-fmouseY, this, this)
        }
        else if (tools.circle.pressed) {
          circle = new Ellipse(fmouseX, fmouseY, math.max(mouseX-fmouseX, mouseY-fmouseY), math.max(mouseX-fmouseX, mouseY-fmouseY), this, this)
        }
      }
    }
    
    for (shape <- shapes) {                          //Piirretään jo olemassa olevat muodot
      if(shape.isInstanceOf[Array[Line]]) shape.asInstanceOf[Array[Line]].foreach { _.display }
      else if (shape.isInstanceOf[Rectangle]) shape.asInstanceOf[Rectangle].display
      else if (shape.isInstanceOf[Ellipse]) shape.asInstanceOf[Ellipse].display
      else if (shape.isInstanceOf[Text]) shape.asInstanceOf[Text].display
    }
    
    line.foreach { _.display }                       //Piirretään se muoto joka on tällä hetkellä piirrossa
    rectangle.display
    ellipse.display
    circle.display
    
    noStroke                                         //Piirretään nappulat yläreunaan
    fill(255)
    rect(0, 0, width, 25)
    for (button <- buttons) {
      button.update                                  //Päivitetään ne, jos hiiri sattuu olemaan sellaisen päällä
      button.display
    }
    fill(255)
    rect(332, 2, 300, 22)
    stroke(0)
    strokeWeight(1)
    line(5, 25, width-5, 25)                         //Alle vielä viiva erottamaan nappulat piirtoalueesta
    
    shapeColor = tools.shapeColor
    fillColor = tools.fillColor
    penSize = tools.penSize                          //Päivitetään värit ja kynän koko
  }
  
  
  override def mousePressed = {                      //Kun hiiren jokin nappula painetaan alas, päivitetään sitä varten olevat muuttujat
    fmouseX = mouseX
    fmouseY = mouseY
    
  }
  
  override def mouseReleased = {                     //Suoritetaan, kun hiiren nappula nousee ylös
    for (button <- buttons) {                        //Katsotaan onko hiiri jonkun napin päällä, jos on niin painetaan sitä
      if (button.over) button.press
    }
    if (mouseY > 25) {
      if (tools.rectangle.pressed && rectangle.x!=0 && rectangle.y!=0 && rectangle.h!=0 && rectangle.w!=0) {
        shapes = shapes:+rectangle
        rectangle = new Rectangle(0, 0, 0, 0, this, this)
      }
      else if (tools.pen.pressed && !line.isEmpty) {
        shapes = shapes:+line
        line = Array()
      }
      else if (tools.ellipse.pressed && ellipse.x!=0 && ellipse.y!=0 && ellipse.h!=0 && ellipse.w!=0) {
        shapes = shapes:+ellipse
        ellipse = new Ellipse(0, 0, 0, 0, this, this)
      }
      else if (tools.circle.pressed && circle.x!=0 && circle.y!=0 && circle.h!=0 && circle.w!=0) {
        shapes = shapes:+circle
        circle = new Ellipse(0, 0, 0, 0, this, this)  //Jos jokin työkalu on valittu ja on piirretty uusi muoto, niin lisätään se piirrettäviin ja nollataan tilapäisvarasto
     }
      else if (tools.write.pressed) {
        val text = new JTextField
        val size = new JTextField
        val m = showConfirmDialog(null, Array("Teksti:", text, "Koko: (pixeliä)", size), "Teksti", JOptionPane.OK_CANCEL_OPTION)
        var t = ""                                    //Kysytään minkä tekstin käyttäjä haluaa lisätä ja minkä kokoisena
        var s = 12
        if (text.getText!="") {
          try {
            t = text.getText
            s = size.getText.toInt                    //Koko laitetaan tekstikenttään, jolloin siihen voi periaatteessa kirjoittaa mitä vaan ja toInt ei tykkää siitä,
          } catch {                                   //joten yritetään napata se virhe ja käyttää siinä tapauksessa oletuskokoa
            case toInt: NumberFormatException => println("Wasn't number in size field! Using default size!")
            case _: Throwable => println("Something went wrong, we don't what!")
          }
          shapes = shapes:+new Text(fmouseX, fmouseY, t, s, this, this)
        }
      }
    }
  }
  
  
  override def keyPressed = {                       //Ajetaan joka kerta kun jotain nappulaa painetaan, sitä varten jos käyttäjä sulkee työkalut, niin ikkunan
    if (keyCode == 84) {                            //saa uudestaan painamalla T:tä
      val tW = new ToolWindow
      val frame2 = new javax.swing.JFrame("Tools")
      frame2.getContentPane().add(tW)
      tW.init
      frame2.setResizable(false)
      frame2.pack
      frame2.setVisible(true)
      tools = tW
    }
  }
  
}


