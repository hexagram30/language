class Arabic(Language):
    implements(ILanguage)
    iso639 = "ara"
    def __init__(self):
        super(Arabic, self).__init__()
        self.language = Syntagmata('arabic')


class Hebrew(Language):
    implements(ILanguage)
    iso639 = "heb"
    def __init__(self):
        super(Hebrew, self).__init__()
        self.language = Syntagmata('hebrew')
