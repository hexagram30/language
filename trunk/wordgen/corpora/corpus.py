import os
from os import path
from glob import glob

from wordgen.utils import UTF8File

def getAvailableLanguages():
    """
    >>> from pprint import pprint
    >>> files = getAvailableLanguages()
    >>> files.sort()
    >>> pprint(files)
    ['afrikaner',
     'arabic',
     'chinese',
     'english',
     'french',
     'gaelic',
     'german',
     'hebrew',
     'hindi',
     'japanese',
     'korean',
     'oldenglish',
     'oldenglish_raw',
     'oldnorse',
     'oldnorse_raw',
     'onomatopoetic',
     'russian',
     'sanskrit',
     'spanish']
    """
    base = path.dirname(__file__)
    sources = path.join(base, 'sources', '*.txt')
    return [path.splitext(path.basename(x))[0] for x in glob(sources)]
        

class Corpus(object):
    """
    This object acts as a proxy for textual data files in the corpora,
    specifically, for one language at a time. All of that languages textual
    resources are exposed as attributes of the Corpus object.
    
    >>> c = Corpus('english')
    >>> c.getVowels()
    u'aeiouy'
    >>> c.getConsonants()
    u'bcdfghjklmnpqrstvwxz'
    >>> k = c.getStats().keys()
    >>> k.sort()
    >>> k
    ['FinalType', 'InitialType', 'MedialType']

    """
    def __init__(self, language='', vowels='', consonants=''):
        self.path = os.path.dirname(__file__)
        self.vowelsFile = "%s/vowels/%s.txt" % (self.path, language)
        self.consonantsFile = "%s/consonants/%s.txt" % (self.path, language)
        self.statsFile = "%s/stats/%s.txt" % (self.path, language)
        self.sourceFile = "%s/sources/%s.txt" % (self.path, language)
        self.vowels = u''
        self.consonants = u''
        self.stats = {}
        self.source = u''

    def loadVowels(self, vowels=u''):
        self.vowels = vowels
        if not self.vowels:
            vowels = UTF8File(self.vowelsFile)
            self.vowels = vowels.read()
            vowels.close()

    def loadConsonants(self, consonants=u''):
        self.consonants = consonants
        if not self.consonants:
            consonants = UTF8File(self.consonantsFile)
            self.consonants = consonants.read()
            consonants.close()

    def loadStats(self):
        stats = UTF8File(self.statsFile)
        self.stats = eval(stats.read())
        stats.close()

    def loadSource(self):
        source = UTF8File(self.sourceFile)
        self.source = source.read()
        source.close()

    def getVowels(self):
        if not self.vowels:
            self.loadVowels()
        return self.vowels

    def getConsonants(self):
        if not self.consonants:
            self.loadConsonants()
        return self.consonants

    def getStats(self):
        if not self.stats:
            self.loadStats()
        return self.stats

    def getSource(self):
        if not self.source:
            self.loadSource()
        return self.source

def _test():
    import doctest
    doctest.testmod()
        
if __name__ == "__main__":
    _test()
