# -*- coding: utf-8 -*-

import sys
import codecs
import random

import LangGen

lang = sys.argv[1]
try:
    startsWith = sys.argv[2].split(',')
except IndexError:
    startsWith = None
    #startsWith = [ x.decode('utf-8') for x in ['ó', 'ö']]
    #startsWith = [ x.decode('utf-8') for x in ['él', 'el']]
    #startsWith = [ x.decode('utf-8') for x in ['orð', 'órð', 'örð', 'orþ', 'órþ', 'örþ'] ]

RANGE1 = 2
RANGE2 = 5
TRIES = 1000
count = TRIES * (RANGE2 - RANGE1)
count = 50
filename = 'tmpWordlist.txt'

def checkWord(word, startsWith):
    for part in startsWith:
        if word.startswith(part):
            return True
    return False

def filterWords(words, startsWith):
    return [ x for x in words if checkWord(x, startsWith) ]

def doCycles(startsWith):
    gen = LangGen.CreateWord()
    gen.stats_filename = "corpora/stats/%s.dict" % lang
    gen.openPickledData()
    words = []
    for j in xrange(TRIES):
        for i in range(RANGE2-RANGE1):
            words.append(gen.getWord(i + RANGE1))
    if startsWith:
        print "Original word count: %s" % count
        words = filterWords(words, startsWith)
        while len(words) < count:
            print "Current filtered word count: %s" % len(words)
            parts = random.randint(RANGE1, RANGE2)
            word = gen.getWord(parts)
            if checkWord(word, startsWith):
                words.append(word)
        print "Final filtered word count: %s" % len(words)
    return words

words = dict([(x, x) for x in doCycles(startsWith)]).keys()
fh = codecs.open(filename, 'w+', 'utf-8')
fh.write('\n'.join(words))
fh.close()
