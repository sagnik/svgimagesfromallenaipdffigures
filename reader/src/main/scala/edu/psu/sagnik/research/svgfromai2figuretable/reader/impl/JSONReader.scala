package edu.psu.sagnik.research.svgfromai2figuretable.reader.impl

/**
 * Created by szr163 on 11/3/15.
 */

import edu.psu.sagnik.research.svgfromai2figuretable.data._
import org.json4s.native.JsonMethods._

object JSONReader {
  implicit val formats = org.json4s.DefaultFormats

  def apply(figJSONFile: String):AllenAIImage = jsonToImageCaseClass(jsonToString(figJSONFile))

  def jsonToString(figJSONFile: String): String = scala.io.Source.fromFile(figJSONFile).mkString

  def jsonToImageCaseClass(jsonStr: String): AllenAIImage = parse(jsonStr).extract[AllenAIImage]


}
