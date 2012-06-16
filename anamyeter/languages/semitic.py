class Arabic(Language):
    implements(ILanguage)
    def __init__(self):
        super(Arabic, self).__init__()
        self.language = Syntagmata('arabic')


class Hebrew(Language):
    implements(ILanguage)
    def __init__(self):
        super(Hebrew, self).__init__()
        self.language = Syntagmata('hebrew')
