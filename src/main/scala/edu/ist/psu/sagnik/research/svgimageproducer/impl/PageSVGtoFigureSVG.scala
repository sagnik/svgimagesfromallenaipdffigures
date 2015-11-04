package edu.ist.psu.sagnik.research.svgimageproducer.impl

import java.io.File

import edu.ist.psu.sagnik.research.svgimageproducer.model._
import edu.ist.psu.sagnik.research.svgimageproducer.model.Rectangle._
import edu.ist.psu.sagnik.research.svgimageproducer.reader.{JSONReader, SVGReader}
import scala.xml.Node

/**
 * Created by szr163 on 11/3/15.
 */
object PageSVGtoFigureSVG {

  val badRectangle=Rectangle(-30,-30,-30,-30)
  val DPICONSTANT=0.72f //assuming original DPI in allenAI JSON (this should actually be called PPI) is 100.

  def apply(pdfLoc:String,figureLoc:String)=
    if (!SVGReader(pdfLoc)) None
    else{
      val pageSVGFiles=DirContent(new File(pdfLoc.substring(0,pdfLoc.length-4)),"page\\d+.svg"r).map(x=>x.getAbsolutePath)
      val allenAIFig=JSONReader(figureLoc)
      (allenAIFig.ImageBB, allenAIFig.Page) match {
        case (Some(bb),Some(pNum)) => Some(SVGImageCaption(allenAIFig.Caption,allenAIFig.Mention,rectFromFloatSeq(pixelToPoints(bb)),getFigureSVG(pixelToPoints(bb),pNum,pageSVGFiles),pNum))
        case _ => None
      }
    }

  def pixelToPoints(bb:Seq[Int]):Seq[Float]= bb.map(x=>x.toFloat*DPICONSTANT)

  /*
  TODO: Reading SVG path strings by splitting the text on newlines. This is a wrong approach. Change this, also, the same thing in pdffigures-scala repo. Test using 10.1.1.353.3090.pdf.
   */

  def getFigureSVG(bb:Seq[Float],pNum:Int, svgFiles:Seq[String])={
    val svgPathsThisPage= svgFiles
      .flatMap(
        a=>{
          val pageNo=a.substring(0,a.length-4).split("-").last.replace("page","").toInt //TODO: possible exception, should be handled
          if (pNum==pageNo) {
            io.Source.fromFile(a).mkString.split("\n").drop(5).dropRight(1).map(x => x.trim)
              .map(
                x => Some(SVGPathString(x,
                  getPathBoundingBox(x),
                  pageNo
                ))
              )
          }
          else None
        }
      ).toIndexedSeq.flatten

    svgPathsThisPage.filter(y=>rectInside(y.bb,rectFromFloatSeq(bb),2f))

  }

  /*This is a bit of hack. We want bounding boxes for characters, raster graphics as well as graphics paths. The algorithm to extract these
  * are different, but in the end they are just bounding boxes
  *
  * */

  def getPathBoundingBox(c:String):Rectangle=
    try {
      if (c.startsWith("<text")&&c.endsWith(">")) {
        //println(c)
        val x = xml.XML.loadString(c).attribute("x")
        val y = xml.XML.loadString(c).attribute("y")
        (x, y) match {
          case (Some(x), Some(y)) => Rectangle(x.text.toFloat, y.text.toFloat, x.text.toFloat + 5, y.text.toFloat + 5) //TODO: Possible exception here.
          case _ => badRectangle
        }
      }
      else if (c.startsWith("<path") && c.endsWith(">")) pathBB(xml.XML.loadString(c).attribute("d"))
      else if (c.startsWith("<image") && c.endsWith(">"))
        imageBB(xml.XML.loadString(c).attribute("transform"), xml.XML.loadString(c).attribute("width"), xml.XML.loadString(c).attribute("height"))
      else {println(s"SVG string parsing error: ${c.substring(0,5)}");badRectangle}
    } catch{
      case x:org.xml.sax.SAXParseException => {println(s"SVG string parsing error: ${c.substring(0,5)}");badRectangle}
    }


  def pathBB(dString:Option[Seq[Node]]):Rectangle=
    dString match{
      case Some(dString)=> {
        val points=dString.text.toLowerCase.split("[lmcz]").map(a=>a.trim).filter(a=>a.length>0)
          .map(a=>a.split("\\s+")).map(a=>point(a(0).toFloat,a(1).toFloat)) //TODO: possible exception
        Rectangle(
          points.map(a=>a.x).min+2f,
          points.map(a=>a.y).min+2f,
          points.map(a=>a.x).max-2f,
          points.map(a=>a.y).max-2f
        )
      }
      case _ => Rectangle(0,0,0,0)
    }

  def imageBB(tmString:Option[Seq[Node]],w:Option[Seq[Node]],h:Option[Seq[Node]]):Rectangle=
    (tmString,w,h) match{
      case (Some(tmString),Some(w),Some(h)) =>
        val tmValues=tmString.text.toLowerCase.replace("matrix(","").replace(")","").split(",")
          .map(x=>x.toFloat)
        //TODO: possible exception(s)
        Rectangle(
          tmValues(4)+2f,
          tmValues(5)+2f,
          tmValues(4)+tmValues(0)*w.text.toFloat -2f,
          tmValues(5)+tmValues(3)*h.text.toFloat -2f
        )
      case _ => {println(s"error getting image bounding box from SVG"); badRectangle}
    }
}
