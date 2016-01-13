package edu.ist.psu.sagnik.research.svgimageproducer.writers

import edu.ist.psu.sagnik.research.inkscapesvgprocessing.model.SVGPath
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.rasterparser.model.SVGRaster
import edu.ist.psu.sagnik.research.inkscapesvgprocessing.textparser.model.SVGChar
import edu.ist.psu.sagnik.research.svgimageproducer.model.{SVGImageCaption}
import scala.reflect.io.File

/**
 * Created by szr163 on 11/4/15.
 */
object SVGWriter {

  def apply(fc:SVGImageCaption,svgLoc:String):Unit=apply(fc,svgLoc,"")

  def apply(figAll:(Option[Seq[SVGPath]],Option[Seq[SVGChar]],Option[Seq[SVGRaster]]),figBB:Seq[Float],svgLoc:String):Unit={

    val figPaths=figAll._1 match{case Some(fps) => fps; case _ => Seq.empty[SVGPath]}
    val figChars=figAll._2 match{case Some(fcs) => fcs; case _ => Seq.empty[SVGChar]}
    val figRasters=figAll._3 match{case Some(fis) => fis; case _ => Seq.empty[SVGRaster]}

    val translateX = -figBB(0)+5
    val translateY= -figBB(1)+5
    val width=figBB(2)-figBB(0)+5
    val height=figBB(3)-figBB(1)+5
    val svgStart="<?xml version=\"1.0\" standalone=\"no\"?>\n\n<svg height=\"" +
      height +
      "\" width=\"" +
      width +
      "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">"+
      "\n"

    val gTransformString="<g transform=\"translate(" +
      translateX +
      "," +
      translateY +
      ")\">"+
      "\n"

    val svgString= figPaths.map(x=>x.pContent).foldLeft("")((a,b)=>a+"\n"+b)+
      "\n"+
      figChars.map(x=>x.charSVGString).foldLeft("")((a,b)=>a+"\n"+b) +
     "\n"+
      figRasters.map(x=>x.imageString).foldLeft("")((a,b)=>a+"\n"+b)

    val svgEnd="\n</g>\n</svg>"

    File(svgLoc).writeAll(svgStart+gTransformString+svgString+svgEnd)

  }
  def apply(figPaths:Seq[SVGPath],chars:Seq[SVGChar],figBB:Seq[Float], svgLoc:String):Unit={
    val translateX = -figBB(0)+5
    val translateY= -figBB(1)+5
    val width=figBB(2)-figBB(0)+5
    val height=figBB(3)-figBB(1)+5
    val svgStart="<?xml version=\"1.0\" standalone=\"no\"?>\n\n<svg height=\"" +
      height +
      "\" width=\"" +
      width +
      "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">"+
      "\n"

    val gTransformString="<g transform=\"translate(" +
      translateX +
      "," +
      translateY +
      ")\">"+
      "\n"

    val svgString=figPaths.map(x=>x.pContent).foldLeft("")((a,b)=>a+"\n"+b)+
    "\n"+
      chars.map(x=>x.charSVGString).foldLeft("")((a,b)=>a+"\n"+b)

    val svgEnd="\n</g>\n</svg>"

    File(svgLoc).writeAll(svgStart+gTransformString+svgString+svgEnd)

  }

  def apply(figPaths:Seq[SVGPath],figBB:Seq[Float], svgLoc:String):Unit={

    val translateX = -figBB(0)+5
    val translateY= -figBB(1)+5

    val width=figBB(2)-figBB(0)+5
    val height=figBB(3)-figBB(1)+5

    val svgStart="<?xml version=\"1.0\" standalone=\"no\"?>\n\n<svg height=\"" +
      height +
      "\" width=\"" +
      width +
      "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">"+
    "\n"

    val gTransformString="<g transform=\"translate(" +
      translateX +
      "," +
      translateY +
      ")\">"+
    "\n"

    val svgString=figPaths.map(x=>x.pContent).foldLeft("")((a,b)=>a+"\n"+b)
    val svgEnd="\n</g>\n</svg>"

    File(svgLoc).writeAll(svgStart+gTransformString+svgString+svgEnd)

  }
  def apply(fc:SVGImageCaption,svgLoc:String,pageSVGLoc:String):Unit={
    //println("in svg writer"+loc)

    val figBB=fc.ImageBB
    val svgStrings=fc.paths.map(x=>x.pathContent)

    val svgHeader="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <svg width=\"" +
      (figBB.x2-figBB.x1).toString +
      "\" height=\"" +
      (figBB.y2-figBB.y1).toString +
      "\" stroke=\"none\" stroke-width=\"0.0\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:svgx=\"http://www.xml-cml.org/schema/svgx\">"

    //TODO: Should handle exceptions here? Also, should the clipPath co ordinates be changed?
    val svgDefString=
      if ("".equalsIgnoreCase(pageSVGLoc)) "\n"
      else
      (xml.XML.loadString(io.Source.fromFile(pageSVGLoc).mkString) \\ "defs").headOption match{
        case Some(y) => y.toString
        case _ => "\n"
      }


    val svgCloser="</svg>"

    val xTranslate=(figBB.x1*(-1)).toString
    val yTranslate=(figBB.y1*(-1)).toString
    val charTranslatePair=("<text ","<text transform=\"translate("+xTranslate+","+yTranslate+")\" ")
    val pathTranslatePair=("<path ","<path transform=\"translate("+xTranslate+","+yTranslate+")\" ")
    val imageTranslatePair=("<image ","<image transform=\"translate("+xTranslate+","+yTranslate+")\" ")

    val transformTranslatePair=("transform=\"", "transform=\"translate("+xTranslate+","+yTranslate+") ")

    File(svgLoc).writeAll(
      svgHeader+"\n"+
        //svgDefString+"\n"+
        svgStrings.foldLeft("")((a,b)=>
          if (b.contains("transform")) a+b.replaceAll(transformTranslatePair._1,transformTranslatePair._2)+"\n"
          else
            a+ b.replaceAll(charTranslatePair._1,charTranslatePair._2)
              .replaceAll(pathTranslatePair._1,pathTranslatePair._2)
              .replaceAll(imageTranslatePair._1,imageTranslatePair._2)
              +"\n")+
        svgCloser

    )
  }

}
