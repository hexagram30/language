class Chinese(Language):
    implements(ILanguage)
    iso639 = "zho"
    def __init__(self):
        super(Chinese, self).__init__()
        self.language = Syntagmata('chinese')
    
    def makeWord(self, syllableCount):
        return self.language.makeCVWord(syllableCount)
