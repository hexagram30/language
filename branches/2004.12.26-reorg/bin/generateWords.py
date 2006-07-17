import sys
import codecs

import LangGen

lang = sys.argv[1]
try:
    startsWith = sys.argv[2].split(',')
except IndexError:
    startsWith = None
    startsWith = [ x.decode('utf-8') for x in ['ó', 'ö']]

RANGE1 = 3
RANGE2 = 6
TRIES = 50
filename = 'tmpWordlist.txt'
def doCycles():
    words = []
    for j in xrange(TRIES):
        for i in range(RANGE2-RANGE1):
            gen = LangGen.CreateWord()
            gen.stats_filename = "corpora/stats/%s.dict" % lang
            gen.openPickledData()
            words.append(gen.getWord(i + RANGE1))
    return words

def filterWords(words):
    newWords = []
    for word in words:
        for part in startsWith:
            if word.find(part) == 0:
                newWords.append(word)
    return newWords

words = doCycles()
count = len(words)
if startsWith:
    words = filterWords(words)
    while len(words) < count:
        words.extend(doCycles())
        words = filterWords(words)
        print "Word count: %s" % len(words)

fh = codecs.open(filename, 'w+', 'utf-8')
for word in words:
    fh.write(word+'\n')
fh.close()
