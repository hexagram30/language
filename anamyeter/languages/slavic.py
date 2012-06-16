class Russian(Language):
    implements(ILanguage)
    iso639 = "rus"
    def __init__(self):
        super(Russian, self).__init__()
        self.language = Syntagmata('russian')
