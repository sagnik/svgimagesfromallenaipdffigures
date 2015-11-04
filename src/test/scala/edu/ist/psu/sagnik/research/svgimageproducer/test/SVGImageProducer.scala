package edu.ist.psu.sagnik.research.svgimageproducer.test

import edu.ist.psu.sagnik.research.svgimageproducer.impl.PageSVGtoFigureSVG
import edu.ist.psu.sagnik.research.svgimageproducer.writers.SVGWriter
import edu.ist.psu.sagnik.research.svgimageproducer.model.Rectangle._

import org.scalatest.FunSpec

/**
 * Created by szr163 on 11/3/15.
 */
class SVGImageProducerTest extends FunSpec {

  describe("testing the code by producing an SVG image") {

    import edu.ist.psu.sagnik.research.svgimageproducer.test.DataLocation._

    it("should produce an SVG image") {
      val svgImageCaption=PageSVGtoFigureSVG(pdfLoc,figureJSONLoc)
      svgImageCaption match{
        case Some(svgImageCaptionNN) =>
        {
          SVGWriter(svgImageCaptionNN,figureJSONLoc.substring(0,figureJSONLoc.length-4)+"svg")
          println(s"[imageBB]: ${asCoordinatesStr(svgImageCaptionNN.ImageBB)} [page number]: ${svgImageCaptionNN.pNum} " +
            s"[produced svg file at]: ${figureJSONLoc.substring(0,figureJSONLoc.length-4)+"svg"}")
        }
        case _ => assert(false)
      }
    }
  }

}
