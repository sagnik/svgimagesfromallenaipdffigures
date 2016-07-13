package edu.psu.sagnik.research.svgfromai2figuretable.data

/**
  * Created by schoudhury on 7/12/16.
  */
case class AllenAIWord(Rotation: Int, Text: String, TextBB: Seq[Float])

case class AllenAIImage(Caption: Option[String], CaptionBB: Option[Seq[Int]], Page: Option[Int],
                        ImageBB: Option[Seq[Int]], ImageText: Option[Seq[AllenAIWord]], Mention: Option[String], Width:Float, Height:Float)

case class SVGPathString(pathContent:String, bb:Rectangle,pNum:Int)

case class SVGImageCaption(caption: Option[String], mention:Option[String], ImageBB: Rectangle, paths:Seq[SVGPathString], pNum:Int)

case class point (x:Float,y:Float)
