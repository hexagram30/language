import sys

from anamyeter.syntagmata import Syntagmata

lang = sys.argv[1]
syn = Syntagmata(lang)
stats = syn.writeStatsFile()
