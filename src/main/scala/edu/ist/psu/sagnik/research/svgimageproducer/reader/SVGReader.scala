package edu.ist.psu.sagnik.research.svgimageproducer.reader

import java.io.File

import edu.ist.psu.sagnik.research.svgimageproducer.model.DirContent
import org.xmlcml.pdf2svg.PDF2SVGConverter

/**
 * Created by szr163 on 11/3/15.
 */
object SVGReader {
  def apply(pdfLoc:String):Boolean=SVGProducer(pdfLoc)

  def SVGProducer(pdfLoc:String):Boolean={
    if (!new File(pdfLoc.substring(0,pdfLoc.length-4)).exists() || ! new File(pdfLoc.substring(0,pdfLoc.length-4)).isDirectory)
      new File(pdfLoc.substring(0, pdfLoc.length - 4)).mkdir()
    else
      println(s"target directory ${pdfLoc.substring(0, pdfLoc.length - 4)} already exists!")

    val SVGConversionSucceded=
    if (new File(pdfLoc.substring(0,pdfLoc.length-4)).exists() && new File(pdfLoc.substring(0,pdfLoc.length-4)).isDirectory) {
      new PDF2SVGConverter().run(
        "-logger",
        "-infofiles",
        "-logglyphs",
        "-outdir", pdfLoc.substring(0,pdfLoc.length-4),
        pdfLoc
      )
    }
    else false

    val svgFiles=DirContent(new File(pdfLoc.substring(0,pdfLoc.length-4)),"page\\d+.svg"r).map(x=>x.getAbsolutePath)
    !svgFiles.isEmpty && SVGConversionSucceded
  }
  
  
}
