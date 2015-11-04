package edu.ist.psu.sagnik.research.svgimageproducer.model

/**
 * Created by szr163 on 11/3/15.
 */
case class AllenAIWord(Rotation: Int, Text: String, TextBB: Seq[Float])

case class AllenAIImage(Caption: Option[String], CaptionBB: Option[Seq[Int]], Page: Option[Int],
                        ImageBB: Option[Seq[Int]], ImageText: Option[Seq[AllenAIWord]], Mention: Option[String])

case class SVGPathString(pathContent:String, bb:Rectangle,pNum:Int)

case class SVGImageCaption(caption: Option[String], mention:Option[String], ImageBB: Rectangle, paths:Seq[SVGPathString], pNum:Int)

case class point (x:Float,y:Float)
