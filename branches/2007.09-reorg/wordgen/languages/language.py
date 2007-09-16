import random

from wordgen.utils import UTF8File
from wordgen.syntagmata import Syntagmata

class Language(object):

    def __init__(self):
        self.name = self.__class__.__name__
        self.language = None
    
    def makeWord(self, syllableCount):
        return self.language.makeWord(syllableCount)

    def printSelection(self, count=20, maxSyllables=7):
        for i in xrange(count):
            print self.makeWord(random.randint(1, maxSyllables))

    def createWordlist(self, filename='tempWordlist.txt', count=250,
                       maxSyllables=7):
        if self.name:
            filename='tmp/%sWordlist.txt' % self.name
        data = []
        for i in xrange(count):
            word = self.makeWord(random.randint(1, maxSyllables))
            data.append(word)
        data.sort()
        file = UTF8File(filename, 'w+')
        file.write('\n'.join(data))
        file.close()

class English(Language):

    def __init__(self):
        super(English, self).__init__()
        self.language = Syntagmata('english')

class Gaelic(Language):

    def __init__(self):
        super(Gaelic, self).__init__()
        self.language = Syntagmata('gaelic')
