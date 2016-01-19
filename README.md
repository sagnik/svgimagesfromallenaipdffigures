## pdffigure JSON to SVG File 

### Goal 
AllenAI pdffigures (https://github.com/allenai/pdffigures) produces JSON files for figures and tables in a scholarly PDF. The JSON file contains the location of the figure on a page of the PDF. Our input is a directory containing such JSON files and the source PDF file. The output is SVG images for the figures and tables.


### Dependency  

1. **pdftk** : https://www.pdflabs.com/tools/pdftk-server/
2. **InkScape** : https://inkscape.org/en/
3. **inkscape-svg-processing** : https://github.com/sagnik/inkscape-svg-processing . This dependency is automatically handled by sbt.   

Please make sure following commands work on your machine:

1. `pdftk <input pdf> burst output <output-directory-location>/page_%0d.pdf`
This will split the PDF into pages.

2. `inkscape -l <out.svg> <in.pdf>`. This will produce an SVG file froma single page PDF file.
 
### Test 

On sbt console, clean, compile, run. This will run `edu.ist.psu.sagnik.research.svgimageproducer.impl.InkScapeSVGtoFigure`. This will split the input PDF (currently, `hassan.pdf`), convert each page into SVG and then extract the figures and tables from the PDF in SVG format (`tmp-Table/Figure-*.svg`). Every output will be stored in the folder `src/test/resources/hassan/`.  

