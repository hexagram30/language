import os

from wordgen.utils import UTF8File

class Corpus(object):
    """
    This object acts as a proxy for textual data files in the corpora,
    specifically, for one language at a time. All of that languages textual
    resources are exposed as attributes of the Corpus object.
    """
    def __init__(self, language='', vowels='', consonants=''):
        self.path = os.path.dirname(__file__)
        if not vowels:
            self.vowelsFile = "%s/vowels/%s.txt" % (self.path, language)
            vowels = UTF8File(self.vowelsFile)
            self.vowels = eval(vowels.read())
            vowels.close()
        else:
            self.vowels = vowels
        if not consonants:
            self.consonantsFile = "%s/consonants/%s.txt" % (self.path, language)
            consonants = UTF8File(self.consonantsFile)
            self.consonants = eval(consonants.read())
            consonants.close()
        else:
            self.consonants = consonants
        self.stats = None
        self.source = None
        if language:
            self.statsFile = "%s/stats/%s.txt" % (self.path, language)
            stats = UTF8File(self.statsFile)
            self.stats = eval(stats.read())
            stats.close()
            self.sourceFile = "%s/sources/%s.txt" % (self.path, language)
            source = UTF8File(self.sourceFile)
            self.source = eval(source.read())
            source.close()


