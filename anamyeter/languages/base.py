import random
from itertools import islice

from zope.interface import Interface, implements

from anamyeter.utils import UTF8File
from anamyeter.syntagmata import Syntagmata


def getSupportedLanguages():
    # XXX this is now broken, due to the module moves; fixing it will probably
    # require the inspect module
    from anamyeter import languages

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

    def __init__(self, langName=''):
        super(Composite, self).__init__()
        self.parts = {}
        self.langName = langName

    def addLanguage(self, langArg, parts=1):
        syn = getSyntagmata(langArg)
        if not self.language:
            self.language = syn * parts
        else:
            self.language += (syn * parts)
        self.parts.setdefault(syn.langName, 0)
        self.parts[syn.langName] += parts
        self.language.langName = self.langName

    def report(self):
        totalParts = sum(self.parts.values())
        for key, val in self.parts.items():
            ratio = val/float(totalParts)
            percent = int(100*ratio)
            print "%s: %i parts (%i%%)" % (key, val, percent)
