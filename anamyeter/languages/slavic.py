class Russian(Language):
    implements(ILanguage)
    def __init__(self):
        super(Russian, self).__init__()
        self.language = Syntagmata('russian')
