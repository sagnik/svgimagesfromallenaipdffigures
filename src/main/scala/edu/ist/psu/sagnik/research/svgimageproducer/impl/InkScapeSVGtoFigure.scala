package edu.ist.psu.sagnik.research.svgimageproducer.impl

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.impl.{SVGTextExtract, SVGPathExtract, SVGRasterExtract}

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.SVGPath
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.rasterparser.model.SVGRaster
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.textparser.model.SVGChar
import edu.ist.psu.sagnik.research.svgimageproducer.reader.JSONReader

import java.io.File

import edu.ist.psu.sagnik.research.svgimageproducer.writers.SVGWriter

/**
 * Created by szr163 on 12/12/15.
 */
object InkScapeSVGtoFigure {
  def apply(figJSONLoc:String, pageSVGLoc:String):(Option[Seq[SVGPath]], Option[Seq[SVGChar]], Option[Seq[SVGRaster]])={
    val fig=JSONReader(figJSONLoc)
    val pageSVGConent=scala.xml.XML.loadFile(new File(pageSVGLoc))
    val fpWdRatio=fig.Width / (pageSVGConent \@ "width").toFloat //TODO: possible exception
    val fpHtRatio=fig.Height / (pageSVGConent \@ "height").toFloat //TODO: possible exception
    if (scala.math.abs(fpWdRatio-fpHtRatio)>0.1) {println("error"); (None,None,None)}
    else {
      //delibartely increasing figureBB.
      val figBB = fig.ImageBB match {
        case Some(bb) => Rectangle(
          bb(0) / fpWdRatio -5f,
          bb(1) / fpWdRatio -5f,
          bb(2) / fpWdRatio +5f,
          bb(3) / fpWdRatio +5f
        )
        case _ => Rectangle(0, 0, 0, 0)
      }
      //println(figBB)
      //SVGTextExtract(pageSVGLoc).foreach(x=>println(s"${x.content}, ${x.bb}"))
      (
        Some(
          SVGPathExtract(pageSVGLoc).filter(a =>
            a.bb match{
              case Some(pathBB) => Rectangle.rectInside(pathBB,figBB)
              case _ => false
            }
          )
        ),
        Some(SVGTextExtract(pageSVGLoc).filter( a => Rectangle.rectInside(a.bb,figBB))),
        Some(SVGRasterExtract(pageSVGLoc).filter( a => Rectangle.rectInside(a.bb,figBB)))
        )
    }
  }

  def main(args: Array[String])= {
//    val figJsonLoc = "src/test/resources/10.1.1.101.912-Figure-2-mod.json"
//    val pageSVGLoc = "src/test/resources/pg_0006.svg"
//    val svgLoc = "src/test/resources/test2.svg"

//    val figJsonLoc = "src/test/resources/10.1.1.101.912-Figure-2-mod.json"
//    val pageSVGLoc = "src/test/resources/pg_0006.svg"
//    val svgLoc = "src/test/resources/test2.svg"

    val figJsonLoc = "src/test/resources/pdffigures-extraction/tmp-Figure-1.json"
    val pageSVGLoc = "src/test/resources/pdffigures/page_2.svg"
    val svgLoc = "src/test/resources/test3.svg"

    val figJson=JSONReader(figJsonLoc)
    figJson.ImageBB match {
      case Some(bb) => {
        val pageSVGConent = scala.xml.XML.loadFile(new File(pageSVGLoc))
        val fpWdRatio = (figJson.Width) / (pageSVGConent \@ "width").toFloat //TODO: possible exception
        val fpHtRatio = (figJson.Height) / (pageSVGConent \@ "height").toFloat //TODO: possible exception
        if (scala.math.abs(fpWdRatio - fpHtRatio) > 0.1) {
          println("Ratio in figure and svg page doesn't match. Not processing anymore.");
          sys.exit(1)
        }
        else {
          val newBB = List(
            bb(0) / fpWdRatio,
            bb(1) / fpWdRatio,
            bb(2) / fpWdRatio,
            bb(3) / fpWdRatio
          )
          val figPaths = InkScapeSVGtoFigure(figJsonLoc, pageSVGLoc)
          SVGWriter(figPaths,newBB, svgLoc)
//          figPaths match {
//            case (Some(paths),Some(chars), Some(image)) => {
//              println(s"[path length]: ${paths.length}, [char length]: ${chars.length}");
//              SVGWriter(paths, chars, image, newBB, svgLoc)
//            }
//            case (Some(paths),Some(chars)) => {
//              println(s"[path length]: ${paths.length}, [char length]: ${chars.length}");
//              SVGWriter(paths, chars, newBB, svgLoc)
//            }
//            case (Some(paths),None) => {SVGWriter(paths,newBB, svgLoc)
//            case _ => println("No path found")
//          }
        }
      }
      case _ => println("couldn't get fig bb")

    }
  }


}
