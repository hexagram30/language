import random
from itertools import islice

from zope.interface import Interface, implements

from wordgen.utils import UTF8File
from wordgen.syntagmata import Syntagmata

class ILanguage(Interface):
    """
    This is an interface for marking classes as languages.
    """

def getSupportedLanguages():
    from pprint import pprint
    from wordgen import languages
    langs = []
    for item in dir(languages):
        k = getattr(languages, item)
        try:
            if ILanguage.implementedBy(k):
                langs.append(item)
        except TypeError:
            pass
    langs.sort()
    pprint(langs)

class BaseLanguage(object):
    def __init__(self):
        self.name = self.__class__.__name__
        self.language = None
    
    def makeWord(self, syllableCount):
        return self.language.makeWord(syllableCount)

    def wordList(self, maxSyllables=7):
        while True:
            yield self.makeWord(random.randint(1, maxSyllables))

    def getWordList(self, count=20, maxSyllables=7):
        wordsIter = self.wordList(maxSyllables)
        words = list(islice(wordsIter, count))
        words.sort()
        return words

    def printWordList(self, count=20, maxSyllables=7):
        wordList = self.getWordList(count, maxSyllables)
        print '\n'.join(wordList)

    def saveWordlist(self, filename='tempWordlist.txt', count=250,
                     maxSyllables=7):
        if self.name:
            filename='tmp/%sWordlist.txt' % self.name
        data = self.getWordList(count, maxSyllables)
        fh = UTF8File(filename, 'w+')
        fh.write('\n'.join(data))
        fh.close()

class Composite(BaseLanguage):

    parts = {}

    def addLanguage(self, langName, parts):
        """
        langName is a string representing the language (has to be in
        corproa/*/*.txt).

        parts is an integer representing, of the total parts, how many the one
        being added will account for.
        """
        lang = Syntagmata(langName)
        if not self.language:
            self.language = lang * parts
        else:
            self.language += (lang * parts)
        self.parts.setdefault(langName, 0)
        self.parts[langName] += parts

    def report(self):
        totalParts = sum(self.parts.values())
        for key, val in self.parts.items():
            ratio = val/float(totalParts)
            percent = int(100*ratio)
            print "%s: %i parts (%i%%)" % (key, val, percent)

class Language(BaseLanguage):
    implements(ILanguage)

class Sanskrit(Language):
    def __init__(self):
        super(Sanskrit, self).__init__()
        self.language = Syntagmata('sanskrit')

class Chinese(Language):
    def __init__(self):
        super(Chinese, self).__init__()
        self.language = Syntagmata('chinese')

class Latin(Language):
    def __init__(self):
        super(Latin, self).__init__()
        self.language = Syntagmata('latin')

class Gaelic(Language):

    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata('gaelic')

class English(Language):
    def __init__(self):
        super(English, self).__init__()
        self.language = Syntagmata('english')

class Afrikaner(Language):
    def __init__(self):
        super(Afrikaner, self).__init__()
        self.language = Syntagmata('afrikaner')

class French(Language):
    def __init__(self):
        super(French, self).__init__()
        self.language = Syntagmata('french')

class Hindi(Language):

    def __init__(self):
        super(Hindi, self).__init__()
        self.language = Syntagmata('hindi')

class Japanese(Language):
    def __init__(self):
        super(Japanese, self).__init__()
        self.language = Syntagmata('japanese')

class Korean(Language):
    def __init__(self):
        super(Korean, self).__init__()
        self.language = Syntagmata('korean')

class Spanish(Language):
    def __init__(self):
        super(Spanish, self).__init__()
        self.language = Syntagmata('spanish')

class Onomatopoetic(Language):
    def __init__(self):
        super(Onomatopoetic, self).__init__()
        self.language = Syntagmata('onomatopoetic')


