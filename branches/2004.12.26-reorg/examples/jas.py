import LangGen

PARTS = 7
RANGE1 = 3
RANGE2 = 12

print "Jas: "
for i in range(RANGE2-RANGE1):
  jas = LangGen.CreateWord()
  jas.stats_filename = "jas/combined_stats.dict"
  jas.openPickledData()
  print jas.getWord(i + RANGE1)
print

russian = LangGen.CreateWord() 
russian.stats_filename = "jas_source/russian_stats.dict"
russian.openPickledData() 
print "Russian: " + russian.getWord(PARTS)

chinese = LangGen.CreateWord()
chinese.stats_filename = "jas_source/chinese_stats.dict" 
chinese.openPickledData()
#print chinese.stats
print "Chinese: " + chinese.getChineseWord(PARTS)

afrik = LangGen.CreateWord()
afrik.stats_filename = "jas_source/afrikaner_stats.dict" 
afrik.openPickledData()
print "Afrikaner: " + afrik.getWord(PARTS)

