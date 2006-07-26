SOURCE	= "mux/combined.txt"
LIST 	= "mux/combined_wordlist.txt"
STATS	= "mux/combined_stats.dict"

import LangGen

stats = LangGen.GenerateStats()
stats.source_filename = SOURCE
stats.list_filname = LIST
stats.stats_filename = STATS

stats.writeWordList()
stat_dict = stats.getWordStats()
print stat_dict
stats.writeStatsFile()
