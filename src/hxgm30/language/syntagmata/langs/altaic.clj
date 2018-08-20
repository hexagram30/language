class Japanese(Language):
    implements(ILanguage)
    iso639 = "jpn"
    def __init__(self):
        super(Japanese, self).__init__()
        self.language = Syntagmata('japanese')


class Korean(Language):
    implements(ILanguage)
    iso639 = "kor"
    def __init__(self):
        super(Korean, self).__init__()
        self.language = Syntagmata('korean')
