# -*- coding: utf-8 -*-

import sys
import codecs

import LangGen

lang = sys.argv[1]
SOURCE  = "corpora/sources/%s.txt" % lang
LIST    = "corpora/wordlists/%s.txt" % lang
STATS   = "corpora/stats/%s.dict" % lang
consonants = codecs.open('corpora/consonants/%s.txt' % lang, 'r', 'utf-8').read()
vowels = codecs.open('corpora/vowels/%s.txt' % lang, 'r', 'utf-8').read()


stats = LangGen.GenerateStats(vowels=vowels, consonants=consonants)
stats.source_filename = SOURCE
stats.list_filname = LIST
stats.stats_filename = STATS

stats.writeWordList()
stats.writeStatsFile()
