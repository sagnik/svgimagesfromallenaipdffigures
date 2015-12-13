package edu.ist.psu.sagnik.research.svgimageproducer.writers

import edu.ist.psu.sagnik.research.svgimageproducer.model.{SVGImageCaption}
import scala.reflect.io.File

/**
 * Created by szr163 on 11/4/15.
 */
object SVGWriter {

  def apply(fc:SVGImageCaption,svgLoc:String):Unit=apply(fc,svgLoc,"")

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
