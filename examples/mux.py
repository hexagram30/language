import LangGen

PARTS = 7
RANGE1 = 3
RANGE2 = 12

print "Mux: "
for i in range(RANGE2-RANGE1):
  mux = LangGen.CreateWord()
  mux.stats_filename = "mux/combined_stats.dict"
  mux.openPickledData()
  print mux.getWord(i + RANGE1)
print


jas = LangGen.CreateWord()
jas.stats_filename = "jas/combined_stats.dict"
jas.openPickledData()
print "Jas: " + jas.getWord(PARTS)

elani = LangGen.CreateWord() 
elani.stats_filename = "elani/combined_stats.dict"
elani.openPickledData() 
print "Elian: " + elani.getWord(PARTS)

rook = LangGen.CreateWord()
rook.stats_filename = "rookish/combined_stats.dict" 
rook.openPickledData()
print "Rookish: " + rook.getWord(PARTS)

