## pdffigure JSON to SVG File 

### Goal 
AllenAI pdffigures (https://github.com/allenai/pdffigures) produces JSON files for figures and tables in a scholarly PDF. The JSON file contains the location of the figure on a page of the PDF. Our input is a directory containing such JSON files and the source PDF file. The output is SVG images for the figures and tables.

### Why?

Most scholarly plots are generated from some data. For example, a line graph is generated from a data table. Sometimes it is extremely beneficial to get that table back. Let's say you have proposed a new method for _key phrase extraction_. This is a fairly common problem in NLP with many standard datasets and methods. Most precious work would report their results in a precision recall graph (a standard line graph in machine learning and IR community). To make a comparison, you have two options: 1. You can redo the experiments or 2. You can extract the data from the existing line graphs in a table, add another column in that (your method) and plot it again. Obviously the benefits are not domain specific, Physics or Chemistry researchers also face the same problem.  

Lot of **semi automatic** tools exist to aid the data extraction process. An excellent tool is WebPlotDigitizer (http://arohatgi.info/WebPlotDigitizer/). We are trying to build a **fully automatic** tool for this purpose. We are not the first, there are multiple papers that have approached this problem. 

Scholarly figures can be embedded in a PDF both in raster and vector graphics format. If you use Latex, you probably include your figures as PNG/ JPEG (raster) or PDF/EPS (vector). In fact, most CS people use vector graphics. We analyzed 40,000 figures from 10,00 papers published in top 50 CS conferences between 2004 and 2014, 74% of them were vector graphics. This number will be even higher in Physics or related domains. 

Most existing papers (and the systems such as the one mentioned before) work on raster graphics. This is not surprising, automatic extraction of vector graphics from PDFs is indeed hard (I won't explain why, but standard commands such as 'pdfimages' can only extract raster graphics from PDFs). Till 2015, there wasn't any tool to **batch extract** vector graphics from PDFs. Then our group and AllenAI came up with two such extractors, AllenAI made their code public and it has gained popularity since. The only problem was that both systems so far would produce a raster image for a vector graphic: they would find the bounding box for a figure, rasterize the PDF (convert the PDF page into a raster image) and crop off the necessary region. But this is a _lossy_ conversion, we are losing the graphics path information from the PDF. 

PDF to SVG is a 'loss less' conversion (the term is used loosely, no conversion is truly lossless) that retains the path information. This can be very helpful for the data extraction problem, we can have 'paths' instead of nasty pixels. That is the purpose of this repository.

Now, how helpful is it, really? Are you really benefitting from extracting the image as an SVG instead of a png? A demo is worth thousand words. So, see this: http://personal.psu.edu/szr163/hassan/hassan-Figure-2.html. 

### Dependency  

1. **pdftk** : https://www.pdflabs.com/tools/pdftk-server/
2. **InkScape** : https://inkscape.org/en/ (We tested on version 0.91, the latest stable version on Ubuntu and version 0.47, Mar 4, 2015, the latest stable version on RedHat that you can get through yum. The version number is important.)
3. **inkscape-svg-processing** : https://github.com/sagnik/inkscape-svg-processing . This dependency is automatically handled by sbt.   

Please make sure following commands work on your machine:

1. `pdftk <input pdf> burst output <output-directory-location>/page_%0d.pdf`
This will split the PDF into pages.

2. `inkscape -l <out.svg> <in.pdf>`. This will produce an SVG file from a single page PDF file.
 
### Test 

If you don't plan to generate the Jar, comment out following two lines in build.sbt:

`1. assemblyJarName in assembly := "pdffigurestosvg.jar"`   
`2. mainClass in assembly := Some("edu.ist.psu.sagnik.research.svgimageproducer.impl.InkScapeSVGtoFigure")`

If you wish to generate the fat jar, keep these files and use the beautiful `sbt-assembly` from https://github.com/sbt/sbt-assembly. 

To see how the code works, on sbt console, clean, compile, run. This will run `edu.ist.psu.sagnik.research.svgimageproducer.impl.InkScapeSVGtoFigure`. This will split the input PDF (currently, `hassan.pdf`), convert each page into SVG and then extract the figures and tables from the PDF in SVG format (`tmp-Table/Figure-*.svg`). Every output will be stored in the folder `src/test/resources/hassan/`.  
