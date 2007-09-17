import random
from itertools import islice

from zope.interface import Interface, implements

from wordgen.utils import UTF8File
from wordgen.syntagmata import Syntagmata

def getSupportedLanguages():
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
    return langs

def printSupportedLanguages():
    print '\n'.join(getSupportedLanguages())

def getSyntagmata(obj):
    if isinstance(obj, str):
        return Syntagmata(obj)
    elif isinstance(obj, Syntagmata):
        return obj
    elif isinstance(obj, Language):
        return obj.language
    elif ILanguage.implementedBy(obj):
        return obj().language

class ILanguage(Interface):
    """
    This is an interface for marking classes as languages.
    """
    pass

class Language(object):

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

class Composite(Language):

    def __init__(self):
        super(Composite, self).__init__()
        self.parts = {}

    def addLanguage(self, langArg, parts=1):
        """
        langName is a string representing the language (has to be in
        corproa/*/*.txt).

        parts is an integer representing, of the total parts, how many the one
        being added will account for.
        """
        syn = getSyntagmata(langArg)
        if not self.language:
            self.language = syn * parts
        else:
            self.language += (syn * parts)
        self.parts.setdefault(syn.langName, 0)
        self.parts[syn.langName] += parts

    def report(self):
        totalParts = sum(self.parts.values())
        for key, val in self.parts.items():
            ratio = val/float(totalParts)
            percent = int(100*ratio)
            print "%s: %i parts (%i%%)" % (key, val, percent)

class Sanskrit(Language):
    implements(ILanguage)
    def __init__(self):
        super(Sanskrit, self).__init__()
        self.language = Syntagmata('sanskrit')

class Chinese(Language):
    implements(ILanguage)
    def __init__(self):
        super(Chinese, self).__init__()
        self.language = Syntagmata('chinese')
    
    def makeWord(self, syllableCount):
        return self.language.makeCVWord(syllableCount)

class Latin(Language):
    implements(ILanguage)
    def __init__(self):
        super(Latin, self).__init__()
        self.language = Syntagmata('latin')

class Gaelic(Language):
    implements(ILanguage)
    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata('gaelic')

class English(Language):
    implements(ILanguage)
    def __init__(self):
        super(English, self).__init__()
        self.language = Syntagmata('english')

class Afrikaner(Language):
    implements(ILanguage)
    def __init__(self):
        super(Afrikaner, self).__init__()
        self.language = Syntagmata('afrikaner')

class French(Language):
    implements(ILanguage)
    def __init__(self):
        super(French, self).__init__()
        self.language = Syntagmata('french')

class Hindi(Language):
    implements(ILanguage)
    def __init__(self):
        super(Hindi, self).__init__()
        self.language = Syntagmata('hindi')

class Japanese(Language):
    implements(ILanguage)
    def __init__(self):
        super(Japanese, self).__init__()
        self.language = Syntagmata('japanese')
    
class Korean(Language):
    implements(ILanguage)
    def __init__(self):
        super(Korean, self).__init__()
        self.language = Syntagmata('korean')

class Spanish(Language):
    implements(ILanguage)
    def __init__(self):
        super(Spanish, self).__init__()
        self.language = Syntagmata('spanish')

class Onomatopoetic(Language):
    implements(ILanguage)
    def __init__(self):
        super(Onomatopoetic, self).__init__()
        self.language = Syntagmata('onomatopoetic')

class Arabic(Language):
    implements(ILanguage)
    def __init__(self):
        super(Arabic, self).__init__()
        self.language = Syntagmata('arabic')

class German(Language):
    implements(ILanguage)
    def __init__(self):
        super(German, self).__init__()
        self.language = Syntagmata('german')

class Hebrew(Language):
    implements(ILanguage)
    def __init__(self):
        super(Hebrew, self).__init__()
        self.language = Syntagmata('hebrew')

class OldNorse(Language):
    implements(ILanguage)
    def __init__(self):
        super(OldNorse, self).__init__()
        self.language = Syntagmata('oldnorse')

class OldEnglish(Language):
    implements(ILanguage)
    def __init__(self):
        super(OldEnglish, self).__init__()
        self.language = Syntagmata('oldenglish')

