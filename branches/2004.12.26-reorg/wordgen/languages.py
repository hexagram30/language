from random import random

from wordgen import UTF8File
from wordgen import Syntagmata

class Language(object):
    
    def makeWord(self, syllableCount):
        return self.language.makeWord(syllableCount)

    def printSelection(self):
        for i in xrange(20):
            print self.makeWord(random.randint(7))

    def createWordlist(self, filename='tempWordlist.txt'):
        data = []
        for i in xrange(250):
            word = self.makeWord(random.randint(7))
            data.append(word)
        data.sort()
        file = UTF8File(filename, '+w')
        file.write('\n'.join(data))
        file.close()

class Orcish(Language):

    def __init__(self):
        # base languages
        german = Syntagmata('german')
        afrikaner = Syntagmata('afrikaner')
        oldnorse = Syntagmata('oldnorse')
        arabic = Syntagmata('arabic')
        chinese = Syntagmata('chinese')
        # mix it up
        self.language = (german * 5 + afrikaner * 7 + 
            oldnorse * 2 + arabic * 8 + chinese * 6)
