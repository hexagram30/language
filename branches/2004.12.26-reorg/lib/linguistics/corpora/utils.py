import os, sys

class Corpora(object):

    def __init__(self, language=''):
        # The commented code doesn't work for Mac OS X, since the path
        # sep is ':'
        #module_path = os.pathsep.join(self.__module__.split('.')[:-1])
        module_path = '/'.join((self.__module__.split('.')[:-1]))
        self.prefix = os.path.join(sys.prefix, module_path)
        self.filename_template = os.path.join(self.prefix, '%s_wordlist.txt')
        self.language = ''
        if language:
            self.loadWordList(language)

    def loadWordList(self, language):
        self.language = language
        filename = self.filename_template % language
        wordlist = file(filename, 'r').read().split('\n')
        self.wordlist = wordlist

    def loadIterWordList(self, language):
        self.language = language
        filename = self.filename_template % language
        self.wordlist = self._iterWordList()

    def _iterWordList(self):
        filename = self.filename_template % self.language
        for word in open(filename, 'r').readlines():
            yield word.strip()
