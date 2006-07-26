import LangGen

PARTS = 7
RANGE1 = 3
RANGE2 = 12

print
print "Rookish: "
for i in range(RANGE2-RANGE1):
  rook = LangGen.CreateWord()
  rook.stats_filename = "rookish/combined_stats.dict"
  rook.openPickledData()
  print rook.getWord(i + RANGE1)
print

english = LangGen.CreateWord() 
english.stats_filename = "rookish_source/english_stats.dict"
english.openPickledData() 
print "English: " + english.getWord(PARTS)

spanish = LangGen.CreateWord()
spanish.stats_filename = "rookish_source/spanish_stats.dict" 
spanish.openPickledData()
#print spanish.stats
print "Spanish: " + spanish.getWord(PARTS)

hindi = LangGen.CreateWord()
hindi.stats_filename = "rookish_source/hindi_stats.dict" 
hindi.openPickledData()
print "Hindi: " + hindi.getWord(PARTS)

japan = LangGen.CreateWord()
japan.stats_filename = "rookish_source/japanese_stats.dict"
japan.openPickledData()
print "Japanese: " + japan.getWord(PARTS)    
print
