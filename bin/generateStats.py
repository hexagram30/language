# -*- coding: utf-8 -*-

import sys

from wordgen.syntagmata import Syntagmata

lang = sys.argv[1]
syn = Syntagmata(lang)
stats = syn.writeStatsFile()
