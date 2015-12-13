package edu.ist.psu.sagnik.research.svgimageproducer.impl

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.impl.SVGPathExtract
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.SVGPath
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle
import edu.ist.psu.sagnik.research.svgimageproducer.reader.JSONReader

import java.io.File

/**
 * Created by szr163 on 12/12/15.
 */
object InkScapeSVGtoFigure {
  def apply(figJSONLoc:String, pageSVGLoc:String):Option[Seq[SVGPath]]={
    val fig=JSONReader(figJSONLoc)
    val pageSVGConent=scala.xml.XML.loadFile(new File(pageSVGLoc))
    val fpWdRatio=fig.Width / (pageSVGConent \@ "width").toFloat //TODO: possible exception
    val fpHtRatio=fig.Height / (pageSVGConent \@ "height").toFloat //TODO: possible exception
    if (scala.math.abs(fpWdRatio-fpHtRatio)>0.1) {println("error"); None}
    else {
      val figBB = fig.ImageBB match {
        case Some(bb) => Rectangle(
          bb(0) / fpWdRatio -10f,
          bb(1) / fpWdRatio -10f,
          bb(2) / fpWdRatio +10f,
          bb(3) / fpWdRatio +10f
        )
        case _ => Rectangle(0, 0, 0, 0)
      }
      Some(
        SVGPathExtract(pageSVGLoc).filter(a =>
          a.bb match{
            case Some(pathBB) => Rectangle.rectInside(pathBB,figBB)
            case _ => false
          }
        )
      )
    }
  }

  def main(args: Array[String])={
    val figJsonLoc="src/test/resources/10.1.1.101.912-Figure-2-mod.json"
    val pageSVGLoc="src/test/resources/pg_0006.svg"
    val figPaths=InkScapeSVGtoFigure(figJsonLoc,pageSVGLoc)
    figPaths match{
      case Some(paths) => paths.foreach(x => println(x.pContent))
      case _ => println(None)
    }
  }
}
