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
