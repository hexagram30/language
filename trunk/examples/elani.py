import LangGen

PARTS = 7
RANGE1= 3
RANGE2= 12

print "Elani: "
for i in range(RANGE2-RANGE1):
  elani = LangGen.CreateWord()
  elani.stats_filename = "elani/combined_stats.dict"
  elani.openPickledData()
  print elani.getWord(i + RANGE1)
print

french = LangGen.CreateWord() 
french.stats_filename = "elani_source/french_stats.dict"
french.openPickledData() 
print "French: " + french.getWord(PARTS)

german = LangGen.CreateWord()
german.stats_filename = "elani_source/german_stats.dict" 
german.openPickledData()
#print chinese.stats
print "German: " + german.getWord(PARTS)

korean = LangGen.CreateWord()
korean.stats_filename = "elani_source/korean_stats.dict" 
korean.openPickledData()
print "Korean: " + korean.getChineseWord(PARTS)

arabic = LangGen.CreateWord()
arabic.stats_filename = "elani_source/arabic_stats.dict"  
arabic.openPickledData()
print "Arabic: " + arabic.getWord(PARTS)   


