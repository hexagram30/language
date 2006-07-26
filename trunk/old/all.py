import LangGen

PARTS = 7
RANGE1 = 3
RANGE2 = 12

dicts = { 
  'English':'rookish_source/english_stats.dict',
  'Spanish':'rookish_source/spanish_stats.dict',
  'Hindi':'rookish_source/hindi_stats.dict',
  'Japanese':'rookish_source/japanese_stats.dict',
  'Frensh':'elani_source/french_stats.dict',
  'German':'elani_source/german_stats.dict',
  'Korean':'elani_source/korean_stats.dict',
  'Arabic':'elani_source/arabic_stats.dict',
  'Russian':'jas_source/russian_stats.dict',
  'Chinese':'jas_source/chinese_stats.dict',
  'Afrikaner':'jas_source/afrikaner_stats.dict',

}
print
for (lang, dict) in dicts.items():
  print "%s: " % (lang)
  for i in range(RANGE2-RANGE1):
    rook = LangGen.CreateWord()
    rook.stats_filename = dict
    rook.openPickledData()
    if lang == 'Korean' or lang == 'Chinese':
      print rook.getChineseWord(i + RANGE1)
    else:
      print rook.getWord(i + RANGE1)
print
