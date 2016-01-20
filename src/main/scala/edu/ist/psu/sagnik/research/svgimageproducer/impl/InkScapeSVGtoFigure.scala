package edu.ist.psu.sagnik.research.svgimageproducer.impl

import java.nio.file.{Paths, Files}

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.impl.{SVGTextExtract, SVGPathExtract, SVGRasterExtract}

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.SVGPath
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.rasterparser.model.SVGRaster
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.textparser.model.SVGChar
import edu.ist.psu.sagnik.research.svgimageproducer.reader.JSONReader

import scala.sys.process._

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


    //    val figJsonLoc = "src/test/resources/pdffigures-extraction/tmp-Figure-1.json"
    //    val pageSVGLoc = "src/test/resources/pdffigures/page_2.svg"
    //    val svgLoc = "src/test/resources/test3.svg"

    //    val figJsonLoc = "src/test/resources/hassan-extraction/tmp-Figure-2.json"
    //    val pageSVGLoc = "src/test/resources/hassan/page_08.svg"
    //    val svgLoc = "src/test/resources/test4.svg"

    val pdfLoc=if (args.length > 0) args(0) else "src/test/resources/hassan.pdf"
    val figJsonDir=pdfLoc.substring(0,pdfLoc.length-4)

    if (Files.exists(Paths.get(figJsonDir))) {
      val jsonFiles = (new File(figJsonDir).listFiles().map(x => x.getAbsolutePath).filter(x => x.endsWith("json"))).toList
      if (jsonFiles.nonEmpty) {
        val splitResult = Seq("pdftk", pdfLoc, "burst", "output", figJsonDir + "/page_%03d.pdf").!
        println(s"[PDF splitting finished with exit code]:${splitResult}")
        if (splitResult == 0) {
          val pagePDFs = (new File(figJsonDir).listFiles().map(x => x.getAbsolutePath).filter(x => x.contains("page_"))).toList
          val svgConversion = pagePDFs.map(x => Seq("inkscape", "-l", x.substring(0, x.length - 4) + ".svg", x).!)
          println(s"[SVG Conversion finished with exit code]:${svgConversion}")
          val jsonMaps = jsonFiles.map(x =>
            (x,
              figJsonDir + "/page_" + {
                JSONReader(x).Page match {
                  case Some(p) => if (p.toString.length == 1) "00" + p.toString else if (p.toString.length == 2) "0" + p.toString else p.toString;
                  case _ => "999"
                }
              } + ".svg",
              x.substring(0, x.length - 5) + ".svg"
              )
          )
          jsonMaps.foreach(x => {
            if (createOneSvg(x._1, x._2, x._3))
              println(s"Created svgFile: ${x._3} jsonFile: ${x._1} pageFile: ${x._2}");
            else
              println(s"Failed to create svgFile: ${x._3} jsonFile: ${x._1} pageFile: ${x._2}");
          }
          )
        }
        else
          println(s"[Couldn't split PDF]: ${pdfLoc}")
      }
      else
        println(s"[No figure/table found for PDF]: ${pdfLoc}")
    }
    else
      println(s"[Output directory doesn't exist for PDF]: ${pdfLoc}")
  }

  def createOneSvg(figJsonLoc:String, pageSVGLoc:String, svgLoc:String):Boolean={
    if (Files.exists(Paths.get(pageSVGLoc)) && Files.exists(Paths.get(figJsonLoc))) {
      val figJson = JSONReader(figJsonLoc)
      figJson.ImageBB match {
        case Some(bb) => {
          val pageSVGConent = scala.xml.XML.loadFile(new File(pageSVGLoc))
          val fpWdRatio = (figJson.Width) / (pageSVGConent \@ "width").toFloat //TODO: possible exception
          val fpHtRatio = (figJson.Height) / (pageSVGConent \@ "height").toFloat //TODO: possible exception
          if (scala.math.abs(fpWdRatio - fpHtRatio) > 0.1) {
            println("Ratio in figure and svg page doesn't match. Not processing anymore.");
            false
          }
          else {
            val newBB = List(
              bb(0) / fpWdRatio,
              bb(1) / fpWdRatio,
              bb(2) / fpWdRatio,
              bb(3) / fpWdRatio
            )
            val figPaths = InkScapeSVGtoFigure(figJsonLoc, pageSVGLoc)
            SVGWriter(figPaths, newBB, svgLoc)
            true
          }
        }
        case _ => {println("couldn't get fig bb");false}
      }
    }
    else
    {println(s"$pageSVGLoc or $figJsonLoc does not exist");false}

  }


}
