## pdffigure JSON to SVG File 

### Goal 
AllenAI pdffigures (https://github.com/allenai/pdffigures) produces JSON files for figures and tables in a scholarly PDF. The JSON file contains the location of the figure on a page of the PDF. Our input is one such JSON file and the source PDF file. The output is the SVG image for the figure.


### Dependency  
Dependencies are included in the distribution itself because they were changed to support the needs.

1. **pdf2svg** : https://bitbucket.org/petermr/pdf2svg

### Test 

On sbt console,

1. clean, compile, test.

2. Change edu.ist.psu.sagnik.research.svgimageproducer.test.DataLocation.scala in `src/test/` to test with new data. 

### TODO

change pdf2svg code to inkscape. Not getting good results from pdf2svg.
 
