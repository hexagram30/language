# -*- coding: utf-8 -*-

import sys
import codecs

from wordgen.syntagmata import Syntagmata

lang = sys.argv[1]
syn = Syntagmata(lang)
stats = syn.writeStatsFile()
